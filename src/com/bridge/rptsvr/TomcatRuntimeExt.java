package com.bridge.rptsvr;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.jdt.launching.*;

/**
 
 * 
 * It is the subclass of TomcatRuntime which choose one fixed version only
 * 
 * <p>The private field 'runtime' cannot be initialized, so it cannot be referred to, 
 * to refer to the one from delegate instead.</p>
 * 
 * @author wmqiang
 * 
 */
public class TomcatRuntimeExt extends TomcatRuntime {

	
	/** 
	 * overwrite it to get the attribute from delegate instead of ownself 
	 * so as getVMInstallTypeId()
	 * 
	 * @see org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime#getVMInstallId()
	 * 
	 * 
	 */
	@Override
	protected String getVMInstallId() {
	
		String id;		
		id = this.getDelegate().valAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
		
		return id;
	}

	@Override
	protected String getVMInstallTypeId() {
		// TODO Auto-generated method stub
		return this.getDelegate().valAttribute(PROP_VM_INSTALL_ID, (String)null);
	}

	private RuntimeType delegate=null;
	public RuntimeType getDelegate() {
		return delegate;
	}

	private final String versionId = "org.eclipse.jst.server.tomcat.runtime.85";
	
	public TomcatRuntimeExt(RuntimeType delegate) {
		super();
		this.delegate = delegate;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getRuntimeClasspath(IPath configPath) {
		
		IPath installPath = this.getDelegate().getRuntime().getLocation();
		// If installPath is relative, convert to canonical path and hope for the best
		if (!installPath.isAbsolute()) {
			try {
				String installLoc = (new File(installPath.toOSString())).getCanonicalPath();
				installPath = new Path(installLoc);
			} catch (IOException e) {
				// Ignore if there is a problem
			}
		}
		return getVersionHandler().getRuntimeClasspath(installPath, configPath);
	}

	/**
	 * overwrite it to set the version by versionId
	 */
	@Override
	public ITomcatVersionHandler getVersionHandler() {		
		
		return TomcatPlugin.getTomcatVersionHandler(versionId);
		
	}

	@Override
	public IStatus validate() {
		IStatus status;
		//don't invoke the super validate, it validate based on its runtime		
		
	
		status = verifyLocation();
		if (!status.isOK())
			return status;
//			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDir, null);
		// don't accept trailing space since that can cause startup problems
		if (getRuntime().getLocation().hasTrailingSeparator())
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDirTrailingSlash, null);
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorJRE, null);

		// check for tools.jar (contains the javac compiler on Windows & Linux) to see whether
		// Tomcat will be able to compile JSPs.
		boolean found = false;
		File file = getVMInstall().getInstallLocation();
		if (file != null) {
			File toolsJar = new File(file, "lib" + File.separator + "tools.jar");
			if (toolsJar.exists())
				found = true;
		}
		
		// on Tomcat 5.5 and later, the Eclipse JDT compiler is used for JSP's
		String id = this.versionId; //getRuntime().getRuntimeType().getId();
		if (!found) {
			if (id != null && (id.indexOf("55") > 0 || id.indexOf("60") > 0 || id.indexOf("70") > 0 || id.indexOf("80") > 0
					|| id.indexOf("85") > 0 || id.indexOf("90") > 0)) {
				found = true;
			}
		}
		
		// on Mac, tools.jar is merged into classes.zip. if tools.jar wasn't found,
		// try loading the javac class by running a check inside the VM
		if (!found) {
			String os = Platform.getOS();
			if (os != null && os.toLowerCase().indexOf("mac") >= 0)
				found = checkForCompiler();
		}
		
		if (!found)
			return new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, Messages.warningJRE, null);
		
		File f = getRuntime().getLocation().append("conf").toFile();
		File[] conf = f.listFiles();
		if (conf != null) {
			int size = conf.length;
			for (int i = 0; i < size; i++) {
				if (!f.canRead())
					return new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, Messages.warningCantReadConfig, null);
			}
		}
		
		
		
		
		if (id != null && id.indexOf("85") > 0) {
			IVMInstall vmInstall = getVMInstall();
			if (vmInstall instanceof IVMInstall2) {
				String javaVersion = ((IVMInstall2)vmInstall).getJavaVersion();
				if (javaVersion != null && !checkVMMinVersion(javaVersion, 107)) {
					return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorJRETomcat85, null);
				}
			}
		}


		return status;
	}
	
	private static Map<String, Integer> javaVersionMap = new ConcurrentHashMap<String, Integer>();
	protected boolean checkVMMinVersion(String javaVersion, int minimumVersion) {
		Integer version = javaVersionMap.get(javaVersion);
		if (version == null) {
			int index = javaVersion.indexOf('.');
			if (index > 0) {
				try {
					int major = Integer.parseInt(javaVersion.substring(0, index)) * 100;
					index++;
					int index2 = javaVersion.indexOf('.', index);
					if (index2 > 0) {
						int minor = Integer.parseInt(javaVersion.substring(index, index2));
						version = new Integer(major + minor);
						javaVersionMap.put(javaVersion, version);
					}
				}
				catch (NumberFormatException e) {
					// Ignore
				}
			}
		}
		// If we have a version, and it's less than the minimum, fail the check
		if (version != null && version.intValue() < minimumVersion) {
			return false;
		}
		return true;
	}

	
	/**
	 * overwrite it to 
	 *  
	 * @see org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime#verifyLocation()
	 */
	@Override
	public IStatus verifyLocation() {
		
		IPath path =this.getDelegate().getRuntime().getLocation();
		
		return this.getVersionHandler().verifyInstallPath(path);
	}

}
