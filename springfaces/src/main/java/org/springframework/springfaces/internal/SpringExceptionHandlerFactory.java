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

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link ExceptionHandlerFactory} that provides integration with Spring.
 * @author Phillip Webb
 */
public class SpringExceptionHandlerFactory extends ExceptionHandlerFactory {

	private final Log logger = LogFactory.getLog(getClass());

	private ExceptionHandlerFactory wrapped;

	public SpringExceptionHandlerFactory(ExceptionHandlerFactory wrapped) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping ExceptionHandlerFactory " + wrapped.getClass()
					+ " to provide integration with Spring");
		}
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandlerFactory getWrapped() {
		return this.wrapped;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new SpringExceptionHandler(getWrapped().getExceptionHandler());
	}
}
