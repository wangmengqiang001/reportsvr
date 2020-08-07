package com.bridge.rptsvr;

import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeWizardFragment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class WizardFragment1 extends WizardFragment {

	protected TomcatRuntimeWizardFragment tomcatWizard;
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		
		Composite comp = tomcatWizard.createComposite(parent, handle);
		//comp.getc
		return comp;
		
	}

	public WizardFragment1() {
		// TODO Auto-generated constructor stub
		tomcatWizard = new TomcatRuntimeWizardFragment();
	}

	@Override
	public boolean hasComposite() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void enter() {
		// TODO Auto-generated method stub
		super.enter();
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return true;
	}

}
