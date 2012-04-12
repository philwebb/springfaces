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
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping ResponseStateManager " + delegate.getClass() + " with renderKitId \""
					+ renderKitId + "\" to provide integration with Spring");
		}
		this.wrapperHandler = new WrapperHandler<ResponseStateManager>(ResponseStateManager.class, delegate) {
			@Override
			protected void postProcessWrapper(ResponseStateManager wrapped) {
				if (wrapped instanceof RenderKitIdAware) {
					((RenderKitIdAware) wrapped).setRenderKitId(renderKitId);
				}
			};
		};
	}

	@Override
	public ResponseStateManager getWrapped() {
		return this.wrapperHandler.getWrapped();
	}
}
