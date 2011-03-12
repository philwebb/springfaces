package org.springframework.springfaces.support;

import java.util.Iterator;

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

public abstract class RenderKitFactoryWrapper extends RenderKitFactory implements FacesWrapper<RenderKitFactory> {

	public abstract RenderKitFactory getWrapped();

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		getWrapped().addRenderKit(renderKitId, renderKit);
	}

	@Override
	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
		return getWrapped().getRenderKit(context, renderKitId);
	}

	@Override
	public Iterator<String> getRenderKitIds() {
		return getWrapped().getRenderKitIds();
	}

}
