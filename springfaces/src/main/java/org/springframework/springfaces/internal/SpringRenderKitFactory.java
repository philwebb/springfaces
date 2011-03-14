package org.springframework.springfaces.internal;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.util.RenderKitFactoryWrapper;

/**
 * A JSF {@link RenderKitFactory} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringRenderKitFactory extends RenderKitFactoryWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private RenderKitFactory delegate;

	public SpringRenderKitFactory(RenderKitFactory delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping RenderKitFactory " + delegate.getClass() + " to provide integration with Spring");
		}
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
}
