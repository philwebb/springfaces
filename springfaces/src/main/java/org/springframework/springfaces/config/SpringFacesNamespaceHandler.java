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
package org.springframework.springfaces.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.util.ReflectionUtils;

/**
 * {@link NamespaceHandler} for Spring Faces configuration namespace.
 * 
 * @author Phillip Webb
 */
public class SpringFacesNamespaceHandler extends NamespaceHandlerSupport {

	private static final String[] DELEGATES = { "org.springframework.springfaces.mvc.config.SpringFacesMvcNamespaceHandler" };

	private SpringFacesNamespaceHandlerContext context = new SpringFacesNamespaceHandlerContext() {

		public void registerBeanDefinitionParser(String elementName, BeanDefinitionParser parser) {
			SpringFacesNamespaceHandler.this.registerBeanDefinitionParser(elementName, parser);
		}
	};

	public void init() {
		registerBeanDefinitionParser("integration", new IntegrationBeanDefinitionParser());
		initDelegates();
	}

	private void initDelegates() {
		try {
			for (String delegate : DELEGATES) {
				initDelegate(delegate);
			}
		} catch (Exception e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
	}

	private void initDelegate(String delegate) throws InstantiationException, IllegalAccessException {
		try {
			Object handlerClass = Class.forName(delegate).newInstance();
			SpringFacesNamespaceHandlerDelegate handler = (SpringFacesNamespaceHandlerDelegate) handlerClass;
			handler.init(this.context);
		} catch (ClassNotFoundException e) {
		}
	}
}
