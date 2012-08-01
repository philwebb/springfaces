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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.springfaces.config.util.BeanDefinitionParserHelper;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectViewIdResolver;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} that parses the <tt>mvc-view-resolver</tt> element to configure a Spring Faces
 * application.
 * @author Phillip Webb
 */
class MvcViewResolverBeanDefinitionParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionParserHelper helper = new BeanDefinitionParserHelper(element, parserContext);
		RootBeanDefinition definition = helper.rootBeanDefinition(BookmarkableRedirectViewIdResolver.class);
		definition.getPropertyValues().add("viewClass", FacesView.class);
		addPropertyFromAttribute(definition, element, "prefix");
		addPropertyFromAttribute(definition, element, "suffix");
		addPropertyFromAttribute(definition, element, "order");
		helper.register(definition);
		return definition;
	}

	private void addPropertyFromAttribute(RootBeanDefinition beanDefinition, Element element, String propertyName) {
		beanDefinition.getPropertyValues().add(propertyName, element.getAttribute(propertyName));
	}
}
