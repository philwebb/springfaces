package org.springframework.springfaces.internal;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;

public class SpringRenderKit extends RenderKitWrapper {

	private String renderKitId;
	SpringResponseStateManager responseStateManager;
	private WrapperHandler<RenderKit> wrapperHandler;

	public SpringRenderKit(String renderKitId, RenderKit delegate) {
		this.renderKitId = renderKitId;
		this.wrapperHandler = WrapperHandler.get(RenderKit.class, delegate);
	}

	@Override
	public RenderKit getWrapped() {
		return wrapperHandler.getWrapped();
	}

	@Override
	public ResponseStateManager getResponseStateManager() {
		if (responseStateManager == null) {
			responseStateManager = new SpringResponseStateManager(renderKitId, super.getResponseStateManager());
		}
		return responseStateManager;
	}
}
