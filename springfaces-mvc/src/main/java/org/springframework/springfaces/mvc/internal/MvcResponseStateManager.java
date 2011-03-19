package org.springframework.springfaces.mvc.internal;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.FacesViewStateHandler;
import org.springframework.springfaces.mvc.view.Renderable;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.util.ResponseStateManagerWrapper;

public class MvcResponseStateManager extends ResponseStateManagerWrapper implements RenderKitIdAware {

	private static final String RENDERING_ATTRIBUTE = MvcResponseStateManager.class.getName() + ".RENDERING";

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
		if (SpringFacesContext.getCurrentInstance() != null && context.getAttributes().containsKey(RENDERING_ATTRIBUTE)
				&& RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(renderKitId)) {
			Renderable renderable = (Renderable) context.getAttributes().get(RENDERING_ATTRIBUTE);
			stateHandler.writeViewState(context, renderable);
		}
		super.writeState(context, state);
	}

	public static void setRendering(FacesContext context, Renderable renderable) {
		if (renderable == null) {
			context.getAttributes().remove(RENDERING_ATTRIBUTE);
		} else {
			context.getAttributes().put(RENDERING_ATTRIBUTE, renderable);
		}

	}
}
