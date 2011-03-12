package org.springframework.springfaces.support;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;

public class SpringRenderKit extends RenderKitWrapper {

	private RenderKit delegate;
	private String renderKitId;

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
		return new SpringResponseStateManager(renderKitId, super.getResponseStateManager());
	}

}
