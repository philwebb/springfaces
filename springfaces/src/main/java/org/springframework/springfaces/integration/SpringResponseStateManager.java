package org.springframework.springfaces.integration;

import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.dunno.SpringFacesUtils;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.render.ResponseStateManagerFactory;
import org.springframework.springfaces.util.ResponseStateManagerWrapper;

public class SpringResponseStateManager extends ResponseStateManagerWrapper {

	private String renderKitId;
	private ResponseStateManager rootDelegate;
	private ResponseStateManager delegate;

	public SpringResponseStateManager(String renderKitId, ResponseStateManager delegate) {
		this.renderKitId = renderKitId;
		this.rootDelegate = delegate;
	}

	public ResponseStateManager getWrapped() {
		if (delegate == null) {
			setupDelegate();
		}
		return delegate;
	}

	private void setupDelegate() {
		delegate = rootDelegate;
		for (ResponseStateManagerFactory factory : SpringFacesUtils.getBeans(ResponseStateManagerFactory.class)) {
			//FIXME log detail
			delegate = factory.newResponseStateManager(delegate);
			if (delegate instanceof RenderKitIdAware) {
				((RenderKitIdAware) delegate).setRenderKitId(renderKitId);
			}
		}
	}

}
