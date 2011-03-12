package org.springframework.springfaces.support;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

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
	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
		RenderKit renderKit = super.getRenderKit(context, renderKitId);
		if (renderKit == null) {
			return null;
		}
		return new SpringRenderKit(renderKitId, renderKit);
	}
}
