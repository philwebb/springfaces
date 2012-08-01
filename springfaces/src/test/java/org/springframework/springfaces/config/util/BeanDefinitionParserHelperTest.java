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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tests for {@link BeanDefinitionParserHelper}.
 * @author Phillip Webb
 */
public class BeanDefinitionParserHelperTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ParserContext parserContext;

	@Mock
	private Element element;

	@Mock
	private Object source;

	@Mock
	private XmlReaderContext readerContext;

	@Mock
	private BeanDefinitionParserDelegate delegate;

	private BeanDefinitionParserHelper helper;

	private String tagName = "tag-name";

	@Captor
	private ArgumentCaptor<ComponentDefinition> componentCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(this.readerContext.extractSource(this.element)).willReturn(this.source);
		given(this.element.getTagName()).willReturn(this.tagName);
		this.parserContext = new ParserContext(this.readerContext, this.delegate);
		this.helper = new BeanDefinitionParserHelper(this.element, this.parserContext);
	}

	@Test
	public void shouldNeedElement() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Element must not be null");
		new BeanDefinitionParserHelper(null, this.parserContext);
	}

	@Test
	public void shouldNeedParserContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ParserContext must not be null");
		new BeanDefinitionParserHelper(this.element, null);
	}

	@Test
	public void shouldGetElement() throws Exception {
		assertThat(this.helper.getElement(), is(this.element));
	}

	@Test
	public void shouldGetParserContext() throws Exception {
		assertThat(this.helper.getParserContext(), is(this.parserContext));
	}

	@Test
	public void shouldGetSource() throws Exception {
		assertThat(this.helper.getSource(), is(this.source));
	}

	@Test
	public void shouldGetComponentDefinition() throws Exception {
		CompositeComponentDefinition componentDefinition = this.helper.getComponentDefinition();
		assertThat(componentDefinition.getName(), is(this.tagName));
		assertThat(componentDefinition.getSource(), is(this.source));
	}

	@Test
	public void shouldCreateRootBeanDefinitionForClass() throws Exception {
		RootBeanDefinition definition = this.helper.rootBeanDefinition(Bean.class);
		assertThat(definition.getBeanClass(), isClass(Bean.class));
		assertThat(definition.getRole(), is(BeanDefinition.ROLE_INFRASTRUCTURE));
		assertThat(definition.getSource(), is(this.source));
	}

	@Test
	public void shouldCreateRootBeanDefinitionForClassAndRole() throws Exception {
		RootBeanDefinition definition = this.helper.rootBeanDefinition(Bean.class, BeanDefinition.ROLE_SUPPORT);
		assertThat(definition.getBeanClass(), isClass(Bean.class));
		assertThat(definition.getRole(), is(BeanDefinition.ROLE_SUPPORT));
		assertThat(definition.getSource(), is(this.source));
	}

	@Test
	public void shouldRegisterClass() throws Exception {
		String name = "bean";
		given(this.readerContext.registerWithGeneratedName(any(BeanDefinition.class))).willReturn(name);
		RegisteredBeanDefinition registeredBeanDefinition = this.helper.register(Bean.class);
		verify(this.readerContext).fireComponentRegistered(this.componentCaptor.capture());
		BeanComponentDefinition beanComponentDefinition = (BeanComponentDefinition) this.componentCaptor.getValue();
		assertThat(beanComponentDefinition.getName(), is(name));
		assertThat(beanComponentDefinition.getBeanDefinition().getBeanClassName(), is(Bean.class.getName()));
		assertThat(beanComponentDefinition.getBeanDefinition().getRole(), is(BeanDefinition.ROLE_INFRASTRUCTURE));
		assertThat(beanComponentDefinition.getBeanDefinition().getSource(), is(this.source));
		assertThat(registeredBeanDefinition.getName(), is(name));
	}

	@Test
	public void shouldRegisterClassWithRole() throws Exception {
		String name = "bean";
		given(this.readerContext.registerWithGeneratedName(any(BeanDefinition.class))).willReturn(name);
		RegisteredBeanDefinition registeredBeanDefinition = this.helper.register(Bean.class,
				BeanDefinition.ROLE_APPLICATION);
		verify(this.readerContext).fireComponentRegistered(this.componentCaptor.capture());
		BeanComponentDefinition beanComponentDefinition = (BeanComponentDefinition) this.componentCaptor.getValue();
		assertThat(beanComponentDefinition.getName(), is(name));
		assertThat(beanComponentDefinition.getBeanDefinition().getBeanClassName(), is(Bean.class.getName()));
		assertThat(beanComponentDefinition.getBeanDefinition().getRole(), is(BeanDefinition.ROLE_APPLICATION));
		assertThat(beanComponentDefinition.getBeanDefinition().getSource(), is(this.source));
		assertThat(registeredBeanDefinition.getName(), is(name));
	}

	@Test
	public void shouldRegisterBeanDefinition() throws Exception {
		String name = "bean";
		BeanDefinition defintion = new RootBeanDefinition(Bean.class);
		given(this.readerContext.registerWithGeneratedName(defintion)).willReturn(name);
		RegisteredBeanDefinition registeredBeanDefinition = this.helper.register(defintion);
		verify(this.readerContext).fireComponentRegistered(this.componentCaptor.capture());
		BeanComponentDefinition beanComponentDefinition = (BeanComponentDefinition) this.componentCaptor.getValue();
		assertThat(beanComponentDefinition.getName(), is(name));
		assertThat(beanComponentDefinition.getBeanDefinition(), is(defintion));
		assertThat(registeredBeanDefinition.getName(), is(name));
	}

	@Test
	public void shouldGetChildBeansOrReferences() throws Exception {
		NodeList childNodes = mock(NodeList.class);
		Element child0 = mock(Element.class);
		given(child0.getLocalName()).willReturn("bean");
		Element child1 = mock(Element.class);
		given(child1.getLocalName()).willReturn("other");
		Element child2 = mock(Element.class);
		given(child2.getLocalName()).willReturn("ref");
		given(childNodes.getLength()).willReturn(3);
		given(childNodes.item(0)).willReturn(child0);
		given(childNodes.item(1)).willReturn(child1);
		given(childNodes.item(2)).willReturn(child2);
		given(this.element.getChildNodes()).willReturn(childNodes);
		Object bean = mock(Object.class, "bean");
		Object ref = mock(Object.class, "ref");
		given(this.delegate.parsePropertySubElement(child0, null)).willReturn(bean);
		given(this.delegate.parsePropertySubElement(child2, null)).willReturn(ref);
		ManagedList<Object> childBeansOrReferences = this.helper.getChildBeansOrReferences(this.element);
		assertThat(childBeansOrReferences.size(), is(2));
		assertThat(childBeansOrReferences.get(0), is(bean));
		assertThat(childBeansOrReferences.get(1), is(ref));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Matcher<Class<?>> isClass(Class<?> type) {
		return (Matcher) equalTo(type);
	}

	public static class Bean {
	}

}
