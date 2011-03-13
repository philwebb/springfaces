package org.springframework.springfaces.integration;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.springframework.springfaces.util.RenderKitFactoryWrapper;


public class SpringRenderKitFactory extends RenderKitFactoryWrapper {

	private RenderKitFactory delegate;

	public SpringRenderKitFactory(RenderKitFactory delegate) {
		this.delegate = delegate;
	}

	@Override
	public RenderKitFactory getWrapped() {
		return delegate;
	}

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		super.addRenderKit(renderKitId, new SpringRenderKit(renderKitId, renderKit));
	}

	//
	//	@Override
	//	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
	//		RenderKit renderKit = super.getRenderKit(context, renderKitId);
	//		if (renderKit == null) {
	//			return null;
	//		}
	//		return new SpringRenderKit(renderKitId, renderKit);
	//	}
}
