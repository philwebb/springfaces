package org.springframework.springfaces.support;

import java.io.IOException;

import javax.faces.FacesWrapper;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

public abstract class ResponseStateManagerWrapper extends ResponseStateManager implements
		FacesWrapper<ResponseStateManager> {

	public abstract ResponseStateManager getWrapped();

	@Override
	public void writeState(FacesContext context, Object state) throws IOException {
		getWrapped().writeState(context, state);
	}

	@Override
	public void writeState(FacesContext context, SerializedView state) throws IOException {
		getWrapped().writeState(context, state);
	}

	@Override
	public Object getState(FacesContext context, String viewId) {
		return getWrapped().getState(context, viewId);
	}

	@Override
	public Object getTreeStructureToRestore(FacesContext context, String viewId) {
		return getWrapped().getTreeStructureToRestore(context, viewId);
	}

	@Override
	public Object getComponentStateToRestore(FacesContext context) {
		return getWrapped().getComponentStateToRestore(context);
	}

	@Override
	public boolean isPostback(FacesContext context) {
		return getWrapped().isPostback(context);
	}

	@Override
	public String getViewState(FacesContext context, Object state) {
		return getWrapped().getViewState(context, state);
	}

}
