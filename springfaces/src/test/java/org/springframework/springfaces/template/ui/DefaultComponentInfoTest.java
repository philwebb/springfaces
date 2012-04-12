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
package org.springframework.springfaces.template.ui;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.expression.el.AbstractELResolver;

import com.sun.el.lang.ExpressionBuilder;
import com.sun.faces.el.ELContextImpl;

/**
 * Tests for {@link DefaultComponentInfo}.
 * 
 * @author Phillip Webb
 */
public class DefaultComponentInfoTest {

	private static final List<UIComponent> NO_COMPONENTS = Collections.emptyList();

	private static final Iterator<FacesMessage> EMPTY_MESSAGES = Collections.<FacesMessage> emptyList().iterator();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FacesContext context;

	private Map<String, Object> applicationMap = new HashMap<String, Object>();

	private Bean bean = new Bean();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(this.context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getApplicationMap()).willReturn(this.applicationMap);
		CompositeELResolver resolver = new CompositeELResolver();
		resolver.add(new TestBeanResolver());
		resolver.add(new BeanELResolver());
		ELContext elContext = new ELContextImpl(resolver);
		given(this.context.getELContext()).willReturn(elContext);
	}

	@Test
	public void shouldNeedContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Context must not be null");
		new DefaultComponentInfo(null, NO_COMPONENTS);
	}

	@Test
	public void shouldNeedComponents() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Components must not be null");
		new DefaultComponentInfo(this.context, null);
	}

	@Test
	public void shouldGetNullComponentIfHasNone() throws Exception {
		ComponentInfo info = new DefaultComponentInfo(this.context, NO_COMPONENTS);
		assertThat(info.getComponent(), is(nullValue()));
	}

	@Test
	public void shouldGetFirstComponent() throws Exception {
		List<UIComponent> components = createComponents(3);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.getComponent(), is(components.get(0)));
	}

	@Test
	public void shouldGetComponents() throws Exception {
		List<UIComponent> components = createComponents(3);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.getComponents(), is(components));
	}

	@Test
	public void shouldBeValidIfAllValid() throws Exception {
		List<UIComponent> components = createComponents(4);
		for (UIComponent component : components) {
			given(((EditableValueHolder) component).isValid()).willReturn(true);
		}
		components.add(2, mock(UIComponent.class));
		given(this.context.getMessages(anyString())).willReturn(EMPTY_MESSAGES);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.isValid(), is(true));
	}

	@Test
	public void shouldNotBeValidIfAnyAreNotValid() throws Exception {
		List<UIComponent> components = createComponents(3);
		for (int i = 0; i < components.size(); i++) {
			UIComponent component = components.get(i);
			given(((EditableValueHolder) component).isValid()).willReturn(i == components.size() - 1);
		}
		components.add(2, mock(UIComponent.class));
		given(this.context.getMessages(anyString())).willReturn(EMPTY_MESSAGES);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.isValid(), is(false));
	}

	@Test
	public void shouldNotBeValidIfHasWarningFacesMessage() throws Exception {
		List<UIComponent> components = createComponentWithFacesMessage(FacesMessage.SEVERITY_WARN);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.isValid(), is(false));
	}

	@Test
	public void shouldBeValidIfHasInfoFacesMessage() throws Exception {
		List<UIComponent> components = createComponentWithFacesMessage(FacesMessage.SEVERITY_INFO);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.isValid(), is(true));
	}

	private List<UIComponent> createComponentWithFacesMessage(FacesMessage.Severity severity) {
		List<UIComponent> components = createComponents(1);
		UIComponent component = components.get(0);
		given(component.getClientId(this.context)).willReturn("cid");
		given(((EditableValueHolder) component).isValid()).willReturn(true);
		List<FacesMessage> messages = Arrays.asList(new FacesMessage(severity, "", ""));
		given(this.context.getMessages("cid")).willReturn(messages.iterator());
		return components;
	}

	@Test
	public void shouldNotBeRequiredIfNoneAreRequired() throws Exception {
		List<UIComponent> components = createComponents(3);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.isRequired(), is(false));
	}

	@Test
	public void shouldBeRequiredIfAnyAreRequired() throws Exception {
		List<UIComponent> components = createComponents(3);
		given(((EditableValueHolder) components.get(2)).isRequired()).willReturn(true);
		components.add(2, mock(UIComponent.class));
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.isRequired(), is(true));
	}

	@Test
	public void shouldBeRequiredIfHasNotNullAnnotation() throws Exception {
		ComponentInfo info = getComponentInfoForBeanValue("notNull");
		assertThat(info.isRequired(), is(true));
	}

	@Test
	public void shouldBeRequiredIfHasNotNullMetaAnnotation() throws Exception {
		ComponentInfo info = getComponentInfoForBeanValue("notBlank");
		assertThat(info.isRequired(), is(true));
	}

	@Test
	public void shouldNotBeRequiredIfConstrainedButNotWithNotNull() throws Exception {
		ComponentInfo info = getComponentInfoForBeanValue("canBeNull");
		assertThat(info.isRequired(), is(false));
	}

	private ComponentInfo getComponentInfoForBeanValue(String property) {
		List<UIComponent> components = createComponents(1);
		UIComponent component = components.get(0);
		ValueExpression value = newValueExpression(property);
		given(component.getValueExpression("value")).willReturn(value);
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		return info;
	}

	@Test
	public void shouldGetNullLabelIfNoComponents() throws Exception {
		ComponentInfo info = new DefaultComponentInfo(this.context, NO_COMPONENTS);
		assertThat(info.getLabel(), is(nullValue()));
	}

	@Test
	public void shouldGetLabelFromFirstComponent() throws Exception {
		List<UIComponent> components = createComponents(2);
		given(components.get(0).getAttributes()).willReturn(
				Collections.<String, Object> singletonMap("label", "component1"));
		given(components.get(1).getAttributes()).willReturn(
				Collections.<String, Object> singletonMap("label", "component2"));
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.getLabel(), is("component1"));
	}

	@Test
	public void shouldGetNullForIfNoComponents() throws Exception {
		ComponentInfo info = new DefaultComponentInfo(this.context, NO_COMPONENTS);
		assertThat(info.getFor(), is(nullValue()));
	}

	@Test
	public void shouldGetForFromComponentClientID() throws Exception {
		List<UIComponent> components = createComponents(2);
		given(components.get(0).getClientId()).willReturn("id1");
		given(components.get(1).getClientId()).willReturn("id2");
		ComponentInfo info = new DefaultComponentInfo(this.context, components);
		assertThat(info.getFor(), is("id1"));
	}

	private List<UIComponent> createComponents(int number) {
		List<UIComponent> components = new ArrayList<UIComponent>();
		for (int i = 0; i < number; i++) {
			UIComponent component = mock(UIComponent.class, withSettings().extraInterfaces(EditableValueHolder.class));
			components.add(component);
		}
		return components;
	}

	private ValueExpression newValueExpression(String propery) {
		return new ExpressionBuilder("#{bean." + propery + "}", this.context.getELContext())
				.createValueExpression(Object.class);
	}

	private class TestBeanResolver extends AbstractELResolver {
		@Override
		protected Object get(String property) {
			if ("bean".equals(property)) {
				return DefaultComponentInfoTest.this.bean;
			}
			return null;
		}
	}

	public static class Bean {

		@NotNull
		private String notNull;

		@NotBlank
		private String notBlank;

		@Min(4)
		@Max(5)
		private String canBeNull;

		public String getNotNull() {
			return this.notNull;
		}

		public void setNotNull(String notNull) {
			this.notNull = notNull;
		}

		public String getNotBlank() {
			return this.notBlank;
		}

		public void setNotBlank(String notBlank) {
			this.notBlank = notBlank;
		}

		public String getCanBeNull() {
			return this.canBeNull;
		}

		public void setCanBeNull(String canBeNull) {
			this.canBeNull = canBeNull;
		}
	}
}
