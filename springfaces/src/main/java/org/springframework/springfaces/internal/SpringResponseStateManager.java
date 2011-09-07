package org.springframework.springfaces.internal;

import javax.faces.render.ResponseStateManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.springfaces.render.ResponseStateManagerWrapper;

/**
 * A JSF {@link ResponseStateManager} that provides integration with Spring.
 * 
 * @see SpringRenderKit
 * 
 * @author Phillip Webb
 */
public class SpringResponseStateManager extends ResponseStateManagerWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private WrapperHandler<ResponseStateManager> wrapperHandler;

	public SpringResponseStateManager(final String renderKitId, ResponseStateManager delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping ResponseStateManager " + delegate.getClass() + " with renderKitId \"" + renderKitId
					+ "\" to provide integration with Spring");
		}
		this.wrapperHandler = new WrapperHandler<ResponseStateManager>(ResponseStateManager.class, delegate) {
			protected void postProcessWrapper(ResponseStateManager wrapped) {
				if (wrapped instanceof RenderKitIdAware) {
					((RenderKitIdAware) wrapped).setRenderKitId(renderKitId);
				}
			};
		};
	}

	public ResponseStateManager getWrapped() {
		return wrapperHandler.getWrapped();
	}
}
