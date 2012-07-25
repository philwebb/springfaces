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
package org.springframework.springfaces.mvc.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.springfaces.config.util.BeanDefinitionParserHelper;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolverChain;
import org.springframework.springfaces.mvc.navigation.ImplicitNavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolverChain;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMethodOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.requestmapped.RequestMappedRedirectDestinationViewResolver;
import org.springframework.springfaces.mvc.render.ClientFacesViewStateHandler;
import org.springframework.springfaces.mvc.servlet.DefaultDestinationViewResolver;
import org.springframework.springfaces.mvc.servlet.DefaultDispatcher;
import org.springframework.springfaces.mvc.servlet.DispatcherAwareBeanPostProcessor;
import org.springframework.springfaces.mvc.servlet.FacesHandlerInterceptor;
import org.springframework.springfaces.mvc.servlet.FacesPostbackHandler;
import org.springframework.springfaces.mvc.servlet.MvcExceptionHandler;
import org.springframework.springfaces.mvc.servlet.SpringFacesFactories;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} that parses the <tt>mvc-support</tt> element to configure a Spring Faces application.
 * 
 * @author Phillip Webb
 */
class MvcSupportBeanDefinitionParser extends AbstractBeanDefinitionParser {

	// FIXME Test

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionParserHelper helper = new BeanDefinitionParserHelper(element, parserContext);
		parserContext.pushContainingComponent(helper.getComponentDefinition());

		// Dispatcher + DispatcherAwareBeanPostProcessor
		RuntimeBeanReference dispatcher = getOrRegister(helper, "dispatcher", DefaultDispatcher.class);
		RootBeanDefinition postProcessor = helper.rootBeanDefinition(DispatcherAwareBeanPostProcessor.class);
		postProcessor.getConstructorArgumentValues().addIndexedArgumentValue(0, dispatcher);
		helper.register(postProcessor);

		// State Handler
		RuntimeBeanReference stateHandler = getOrRegister(helper, "state-handler", ClientFacesViewStateHandler.class);

		// Postback Handler
		RootBeanDefinition postbackHandler = helper.rootBeanDefinition(FacesPostbackHandler.class);
		postbackHandler.getConstructorArgumentValues().addIndexedArgumentValue(0, stateHandler);
		helper.register(postbackHandler);

		// Exception Handler
		helper.register(MvcExceptionHandler.class);

		// Destination View Resolvers
		RuntimeBeanReference destinationViewResolvers = createResolverChain(helper, DestinationViewResolverChain.class,
				"destination-view-resolvers", RequestMappedRedirectDestinationViewResolver.class,
				DefaultDestinationViewResolver.class);

		// Navigation View Resolvers
		RuntimeBeanReference navigationViewResolvers = createResolverChain(helper,
				NavigationOutcomeResolverChain.class, "navigation-outcome-resolvers",
				ImplicitNavigationOutcomeResolver.class, NavigationMethodOutcomeResolver.class);

		// Spring Faces Factories
		RootBeanDefinition factories = helper.rootBeanDefinition(SpringFacesFactories.class);
		factories.getConstructorArgumentValues().addIndexedArgumentValue(0, stateHandler);
		factories.getConstructorArgumentValues().addIndexedArgumentValue(1, destinationViewResolvers);
		factories.getPropertyValues().addPropertyValue("navigationOutcomeResolver", navigationViewResolvers);
		helper.register(factories);

		// Interceptor
		RootBeanDefinition interceptor = helper.rootBeanDefinition(FacesHandlerInterceptor.class);
		RootBeanDefinition mappedInterceptor = helper.rootBeanDefinition(MappedInterceptor.class);
		mappedInterceptor.getConstructorArgumentValues().addIndexedArgumentValue(0, (Object) null);
		mappedInterceptor.getConstructorArgumentValues().addIndexedArgumentValue(1, interceptor);
		helper.register(mappedInterceptor);

		parserContext.popAndRegisterContainingComponent();
		return null;
	}

	private RuntimeBeanReference getOrRegister(BeanDefinitionParserHelper helper, String attribute, Class<?> beanClass) {
		if (helper.getElement().hasAttribute(attribute)) {
			return new RuntimeBeanReference(helper.getElement().getAttribute(attribute));
		}
		return helper.register(beanClass).asReference();
	}

	private RuntimeBeanReference createResolverChain(BeanDefinitionParserHelper helper, Class<?> resolverChainClass,
			String childElementName, Class<?>... defaultResolvers) {
		Element childElement = DomUtils.getChildElementByTagName(helper.getElement(), childElementName);
		ManagedList<Object> resolvers = helper.getChildBeansOrReferences(childElement);
		if (childElement == null || Boolean.valueOf(childElement.getAttribute("register-defaults"))) {
			for (Class<?> defualtResolver : defaultResolvers) {
				resolvers.add(helper.rootBeanDefinition(defualtResolver));
			}
		}
		RootBeanDefinition resolverChain = helper.rootBeanDefinition(resolverChainClass);
		resolverChain.getPropertyValues().add("resolvers", resolvers);
		return helper.register(resolverChain).asReference();
	}
}
