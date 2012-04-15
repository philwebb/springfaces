/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.internal;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.render.RenderKitFactoryWrapper;

/**
 * A JSF {@link RenderKitFactory} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringRenderKitFactory extends RenderKitFactoryWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private RenderKitFactory delegate;

	public SpringRenderKitFactory(RenderKitFactory wrapped) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping RenderKitFactory " + wrapped.getClass()
					+ " to provide integration with Spring");
		}
		this.delegate = wrapped;
		reloadRenderKits();
	}

	private void reloadRenderKits() {
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<String> renderKitIds = getRenderKitIds();
		if (renderKitIds != null) {
			while (renderKitIds.hasNext()) {
				String renderKitId = renderKitIds.next();
				RenderKit renderKit = getRenderKit(context, renderKitId);
				if (renderKit != null) {
					addRenderKit(renderKitId, renderKit);
				}
			}
		}
	}

	@Override
	public RenderKitFactory getWrapped() {
		return this.delegate;
	}

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		if (renderKit instanceof SpringRenderKit) {
			renderKit = ((SpringRenderKit) renderKit).getWrapped();
		}
		super.addRenderKit(renderKitId, new SpringRenderKit(renderKitId, renderKit));
	}
}
