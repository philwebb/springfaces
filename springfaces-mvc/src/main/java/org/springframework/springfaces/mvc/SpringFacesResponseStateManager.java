package org.springframework.springfaces.mvc;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.RenderKitIdAware;
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

	//FIXME should delegate to strategy

	@Override
	public void writeState(FacesContext context, Object state) throws IOException {
		if (RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(renderKitId)
				&& SpringFacesContext.getCurrentInstance().isRendering()) {
			ResponseWriter writer = context.getResponseWriter();
			writer.write("<input type=\"hidden\" id=\"org.spring.springfaces\" name=\"org.spring.springfaces\" value=\"somedata\"");
		}
		super.writeState(context, state);
	}

}
