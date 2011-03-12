package org.springframework.springfaces.dunno;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesHandlerInterceptor;
import org.springframework.springfaces.support.ResponseStateManagerWrapper;

public class SpringFacesResponseStateManager extends ResponseStateManagerWrapper {

	private String renderKitId;
	private ResponseStateManager delegate;

	public SpringFacesResponseStateManager(String renderKitId, ResponseStateManager delegate) {
		this.renderKitId = renderKitId;
		this.delegate = delegate;
	}

	@Override
	public ResponseStateManager getWrapped() {
		return delegate;
	}

	@Override
	public void writeState(FacesContext context, Object state) throws IOException {
		if (RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(renderKitId)
				&& FacesHandlerInterceptor.getContext().isRendering()) {
			ResponseWriter writer = context.getResponseWriter();
			writer.write("<input type=\"hidden\" id=\"org.spring.springfaces\" name=\"org.spring.springfaces\" value=\"somedata\"");
		}
		super.writeState(context, state);
	}

}
