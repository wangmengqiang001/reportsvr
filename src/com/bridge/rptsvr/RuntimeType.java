package com.bridge.rptsvr;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

public class RuntimeType extends RuntimeDelegate {

//Test diff

	public RuntimeType() {
		
	}
	//getAttribute is protected, so define a new public method 
	public String valAttribute(String id, String defaultValue) {
		
		return getAttribute(id, defaultValue);
	}
	@Override
	public IStatus validate() {
		IStatus status =  super.validate();
		if(status == Status.OK_STATUS) {
			//TODO invoke tomcat plugin as specified version to validate 
			TomcatRuntimeExt tomcat = new TomcatRuntimeExt(this);
			status = tomcat.validate();
			
		}	
				
		return status;
	}

}
