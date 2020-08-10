package com.bridge.rptsvr;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.ui.internal.viewers.InitialSelectionProvider;

public class SelectionProvider1 extends InitialSelectionProvider {

	@Override
	public IServer getInitialSelection(IServer[] servers) {
		// TODO Auto-generated method stub
		return super.getInitialSelection(servers);
	}

	@Override
	public IRuntimeType getInitialSelection(IRuntimeType[] runtimeTypes) {
		// TODO Auto-generated method stub
		return super.getInitialSelection(runtimeTypes);
	}

	@Override
	protected boolean hasRuntime(IServerType serverType) {
		// TODO Auto-generated method stub
		return super.hasRuntime(serverType);
	}

	@Override
	protected boolean hasRuntime(IRuntimeType runtimeType) {
		// TODO Auto-generated method stub
		return super.hasRuntime(runtimeType);
	}

	@Override
	protected IServerType getDefaultServerType(IServerType[] serverTypes) {
		// TODO Auto-generated method stub
		return super.getDefaultServerType(serverTypes);
	}

	@Override
	protected IRuntimeType getDefaultRuntimeType(IRuntimeType[] runtimeTypes) {
		// TODO Auto-generated method stub
		return super.getDefaultRuntimeType(runtimeTypes);
	}

	public SelectionProvider1() {
		// TODO Auto-generated constructor stub
	}

}
