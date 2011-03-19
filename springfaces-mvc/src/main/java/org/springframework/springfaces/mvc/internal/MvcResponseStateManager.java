package org.springframework.springfaces.mvc.internal;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.springfaces.util.ResponseStateManagerWrapper;

public class MvcResponseStateManager extends ResponseStateManagerWrapper implements RenderKitIdAware {

	private static final String VIEW_ARTIFACT_ATTRIBUTE = MvcResponseStateManager.class.getName() + ".RENDERING";

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
		if (SpringFacesContext.getCurrentInstance() != null && context.getAttributes().containsKey(VIEW_ARTIFACT_ATTRIBUTE)
				&& RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(renderKitId)) {
			ViewArtifact viewArtifact = (ViewArtifact) context.getAttributes().get(VIEW_ARTIFACT_ATTRIBUTE);
			stateHandler.write(context, viewArtifact);
		}
		super.writeState(context, state);
	}

	public static void prepare(FacesContext context, ViewArtifact viewArtifact) {
		if (viewArtifact == null) {
			context.getAttributes().remove(VIEW_ARTIFACT_ATTRIBUTE);
		} else {
			context.getAttributes().put(VIEW_ARTIFACT_ATTRIBUTE, viewArtifact);
		}

	}
}
