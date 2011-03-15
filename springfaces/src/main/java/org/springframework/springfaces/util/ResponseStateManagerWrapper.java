package org.springframework.springfaces.util;

import java.io.IOException;

import javax.faces.FacesWrapper;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

/**
 * Provides a simple implementation of {@link ResponseStateManager} that can be subclassed by developers wishing to
 * provide specialised behaviour to an existing {@link ResponseStateManager instance} . The default implementation of
 * all methods is to call through to the wrapped {@link ResponseStateManager}.
 * 
 * @author Phillip Webb
 */
@SuppressWarnings("deprecation")
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
