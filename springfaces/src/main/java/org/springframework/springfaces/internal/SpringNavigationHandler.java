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

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.application.ConfigurableNavigationHandlerWrapper;

/**
 * A JSF {@link NavigationHandler} that provides integration with Spring.
 * @author Phillip Webb
 */
public class SpringNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private WrapperHandler<ConfigurableNavigationHandler> wrapperHandler;

	public SpringNavigationHandler(ConfigurableNavigationHandler wrapped) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping NavigationHandler " + wrapped.getClass()
					+ " to provide integration with Spring");
		}
		this.wrapperHandler = WrapperHandler.get(ConfigurableNavigationHandler.class, wrapped);
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return this.wrapperHandler.getWrapped();
	}

}
