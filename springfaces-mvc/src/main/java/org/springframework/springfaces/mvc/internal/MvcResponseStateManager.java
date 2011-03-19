package org.springframework.springfaces.mvc.internal;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.FacesViewStateHandler;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.util.ResponseStateManagerWrapper;

public class MvcResponseStateManager extends ResponseStateManagerWrapper implements RenderKitIdAware {

	private String renderKitId;
	private ResponseStateManager delegate;
	private FacesViewStateHandler stateHandler;

	public MvcResponseStateManager(ResponseStateManager delegate, FacesViewStateHandler stateHandler) {
		this.delegate = delegate;
		this.stateHandler = stateHandler;
	}

	public void setRenderKitId(String renderKitId) {
		this.renderKitId = renderKitId;
	}

	@Override
	public ResponseStateManager getWrapped() {
		return delegate;
	}

	@Override
	public void writeState(FacesContext context, Object state) throws IOException {
		if (RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(renderKitId)) {
			SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
			if (springFacesContext != null && springFacesContext.getRendering() != null) {
				//FIXME getting getRendering is wrong as could be a nav response
				stateHandler.writeViewState(context, springFacesContext.getRendering());
			}
		}
		super.writeState(context, state);
	}
}
