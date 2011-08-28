package org.springframework.springfaces.internal;

import java.util.Iterator;

import javax.faces.context.FacesContext;
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
		reloadRenderKits();
	}

	private void reloadRenderKits() {
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<String> renderKitIds = getRenderKitIds();
		while (renderKitIds.hasNext()) {
			String renderKitId = renderKitIds.next();
			RenderKit renderKit = getRenderKit(context, renderKitId);
			if (renderKit != null) {
				addRenderKit(renderKitId, renderKit);
			}
		}
	}

	@Override
	public RenderKitFactory getWrapped() {
		return delegate;
	}

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		if (renderKit instanceof SpringRenderKit) {
			renderKit = ((SpringRenderKit) renderKit).getWrapped();
		}
		super.addRenderKit(renderKitId, new SpringRenderKit(renderKitId, renderKit));
	}
}
