package org.springframework.springfaces.mvc.internal;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.util.ResponseStateManagerWrapper;

public class SpringFacesResponseStateManager extends ResponseStateManagerWrapper implements RenderKitIdAware {

	private String renderKitId;
	private ResponseStateManager delegate;

	public SpringFacesResponseStateManager(ResponseStateManager delegate) {
		this.delegate = delegate;
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
		if (RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(renderKitId)
				&& SpringFacesContext.getCurrentInstance() != null) {
			SpringFacesContext.getCurrentInstance().writeState(context, state);
		}
		super.writeState(context, state);
	}
}
