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
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping RenderKit " + delegate.getClass() + " with ID " + renderKitId
					+ " to provide integration with Spring");
		}
		this.renderKitId = renderKitId;
		this.wrapperHandler = WrapperHandler.get(RenderKit.class, delegate);
	}

	@Override
	public RenderKit getWrapped() {
		return this.wrapperHandler.getWrapped();
	}

	@Override
	public ResponseStateManager getResponseStateManager() {
		if (this.responseStateManager == null) {
			this.responseStateManager = new SpringResponseStateManager(this.renderKitId,
					super.getResponseStateManager());
		}
		return this.responseStateManager;
	}
}
