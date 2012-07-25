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
package org.springframework.springfaces.config.util;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Helper to aid when writing {@link BeanDefinitionParser}s.
 * @author Phillip Webb
 */
public class BeanDefinitionParserHelper {

	// FIXME Test

	private Element element;
	private ParserContext parserContext;
	private Object source;

	/**
	 * Create a new {@link BeanDefinitionParserHelper} instance.
	 * @param element the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
	 * @param parserContext the object encapsulating the current state of the parsing process; provides access to a
	 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 */
	public BeanDefinitionParserHelper(Element element, ParserContext parserContext) {
		Assert.notNull(element, "Element must not be null");
		Assert.notNull(parserContext, "ParserContext must not be null");
		this.element = element;
		this.parserContext = parserContext;
		this.source = parserContext.extractSource(element);
	}

	/**
	 * Returns the element.
	 * @return the element
	 */
	public Element getElement() {
		return this.element;
	}

	/**
	 * Returns the parser context.
	 * @return the parserContext
	 */
	public ParserContext getParserContext() {
		return this.parserContext;
	}

	/**
	 * Returns the source.
	 * @return the source
	 */
	public Object getSource() {
		return this.source;
	}

	/**
	 * Return a {@link CompositeComponentDefinition} for this element.
	 * @return the composite component definition
	 */
	public CompositeComponentDefinition getComponentDefinition() {
		return new CompositeComponentDefinition(this.element.getTagName(), this.source);
	}

	/**
	 * Create a new {@link RootBeanDefinition} with <tt>ROLE_INFRASTRUCTURE</tt> linked to the {@link #getSource()
	 * source}.
	 * @param beanClass the bean class to create
	 * @return the bean definition
	 */
	public RootBeanDefinition rootBeanDefinition(Class<?> beanClass) {
		return rootBeanDefinition(beanClass, BeanDefinition.ROLE_INFRASTRUCTURE);
	}

	/**
	 * Create a bew {@link RootBeanDefinition} with the specified <tt>role</tt> linked to the {@link #getSource()
	 * source}.
	 * @param beanClass the bean class to create
	 * @param role the role
	 * @return the bean definition
	 */
	public RootBeanDefinition rootBeanDefinition(Class<?> beanClass, int role) {
		Assert.notNull(beanClass, "The beanClass must not be null");
		RootBeanDefinition definition = new RootBeanDefinition(beanClass);
		definition.setRole(role);
		definition.setSource(this.source);
		return definition;
	}

	/**
	 * Register the specified bean class with <tt>ROLE_INFRASTRUCTURE</tt>
	 * @param beanClass the class of the bean to register
	 * @return the bean reference
	 */
	public RegisteredBeanDefinition register(Class<?> beanClass) {
		return register(beanClass, BeanDefinition.ROLE_INFRASTRUCTURE);
	}

	/**
	 * Register the specified bean class with the given role
	 * @param beanClass the bean class
	 * @param role the role
	 * @return the registered bean definition
	 */
	public RegisteredBeanDefinition register(Class<?> beanClass, int role) {
		RootBeanDefinition definition = rootBeanDefinition(beanClass, role);
		return register(definition);
	}

	/**
	 * Register the specified bean definition.
	 * @param definition the bean definition
	 * @return the registered bean definition
	 */
	public RegisteredBeanDefinition register(BeanDefinition definition) {
		Assert.notNull(definition, "Definition must not be null");
		String name = this.parserContext.getReaderContext().registerWithGeneratedName(definition);
		this.parserContext.registerComponent(new BeanComponentDefinition(definition, name));
		return new RegisteredBeanDefinition(definition, name);
	}

	/**
	 * Return all child <tt>bean</tt> and <tt>ref</tt> elements of the specified <tt>element</tt>.
	 * @param element the child element or <tt>null</tt>
	 * @return a new managed list linked to the {@link #getSource() source} bean or references. The returned list is
	 * mutable and may be added to by the caller. If the <tt>element</tt> is <tt>null</tt> or no child items are found
	 * an empty list is returned.
	 */
	public ManagedList<Object> getChildBeansOrReferences(Element element) {
		ManagedList<Object> list = new ManagedList<Object>();
		list.setSource(getSource());
		if (element != null) {
			List<Element> elements = DomUtils.getChildElementsByTagName(element, new String[] { "bean", "ref" });
			for (Element child : elements) {
				Object bean = getParserContext().getDelegate().parsePropertySubElement(child, null);
				list.add(bean);
			}
		}
		return list;
	}
}
