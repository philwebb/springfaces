package org.springframework.springfaces.internal;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link RenderKit} that provides integration with Spring.
 * 
 * @see SpringRenderKitFactory
 * 
 * @author Phillip Webb
 */
public class SpringRenderKit extends RenderKitWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private String renderKitId;
	SpringResponseStateManager responseStateManager;
	private WrapperHandler<RenderKit> wrapperHandler;

	public SpringRenderKit(String renderKitId, RenderKit delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping RenderKit " + delegate.getClass() + " with ID " + renderKitId
					+ " to provide integration with Spring");
		}
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
