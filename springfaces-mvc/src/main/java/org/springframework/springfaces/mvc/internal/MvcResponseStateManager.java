package org.springframework.springfaces.mvc.internal;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.FacesViewStateHandler;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.render.ResponseStateManagerWrapper;

/**
 * A JSF {@link ResponseStateManager} that provides integration with Spring MVC.
 * 
 * @author Phillip Webb
 */
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
		return this.delegate;
	}

	@Override
	public void writeState(FacesContext context, Object state) throws IOException {
		if (SpringFacesContext.getCurrentInstance() != null
				&& SpringFacesContext.getCurrentInstance().getRendering() != null
				&& RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(this.renderKitId)) {
			ViewArtifact viewArtifact = SpringFacesContext.getCurrentInstance().getRendering().getViewArtifact();
			this.stateHandler.write(context, viewArtifact);
		}
		super.writeState(context, state);
	}
}
