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

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link RenderKit} that provides integration with Spring.
 * 
 * @author Phillip Webb
 * @see SpringRenderKitFactory
 */
public class SpringRenderKit extends RenderKitWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private String renderKitId;
	SpringResponseStateManager responseStateManager;
	private WrapperHandler<RenderKit> wrapperHandler;

	public SpringRenderKit(String renderKitId, RenderKit wrapped) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping RenderKit " + wrapped.getClass() + " with ID " + renderKitId
					+ " to provide integration with Spring");
		}
		this.renderKitId = renderKitId;
		this.wrapperHandler = WrapperHandler.get(RenderKit.class, wrapped);
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
