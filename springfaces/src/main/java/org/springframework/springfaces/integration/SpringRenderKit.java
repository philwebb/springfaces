package org.springframework.springfaces.integration;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;


public class SpringRenderKit extends RenderKitWrapper {

	private RenderKit delegate;
	private String renderKitId;
	private ResponseStateManager responseStateManager;

	public SpringRenderKit(String renderKitId, RenderKit delegate) {
		this.renderKitId = renderKitId;
		this.delegate = delegate;
	}

	@Override
	public RenderKit getWrapped() {
		return delegate;
	}

	@Override
	public ResponseStateManager getResponseStateManager() {
		if (responseStateManager == null) {
			responseStateManager = new SpringResponseStateManager(renderKitId, super.getResponseStateManager());
		}
		return responseStateManager;
	}
}
