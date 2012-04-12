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
package org.springframework.springfaces.convert;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.faces.FacesWrapper;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.springfaces.bean.ForClass;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * Tests for {@link SpringFacesConverterSupport}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesConverterSupportTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private SpringFacesConverterSupport converterSupport = new SpringFacesConverterSupport();

	private StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();

	@Mock
	private Application delegateApplication;

	@Mock
	private FacesContext facesContext;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
		FacesContextSetter.setCurrentInstance(this.facesContext);
	}

	@After
	public void teardown() {
		FacesContextSetter.setCurrentInstance(null);
	}

	private Application createWrappedApplication() {
		this.converterSupport.setApplicationContext(this.applicationContext);
		this.converterSupport.onApplicationEvent(new ContextRefreshedEvent(this.applicationContext));
		return this.converterSupport.newWrapper(Application.class, this.delegateApplication);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGetWrapped() throws Exception {
		Application application = createWrappedApplication();
		assertThat(((FacesWrapper<Application>) application).getWrapped(), is(this.delegateApplication));
	}

	@Test
	public void shouldDelegateCreateConverterById() throws Exception {
		Application application = createWrappedApplication();
		Converter converter = mock(Converter.class);
		given(this.delegateApplication.createConverter("id")).willReturn(converter);
		Converter actual = application.createConverter("id");
		assertThat(actual, is(sameInstance(converter)));
	}

	@Test
	public void shouldCreateConverterByIdFromSpring() throws Exception {
		this.applicationContext.registerSingleton("bean", MockConverter.class);
		Application application = createWrappedApplication();
		Converter converter = application.createConverter("bean");
		assertThat(converter, is(instanceOf(SpringBeanConverter.class)));
		assertThat(getWrapped(converter), is(instanceOf(MockConverter.class)));
	}

	@Test
	public void shouldCreateFacesConverterByIdFromSpring() throws Exception {
		this.applicationContext.registerSingleton("bean", MockFacesConverter.class);
		Application application = createWrappedApplication();
		Converter converter = application.createConverter("bean");
		assertThat(converter, is(instanceOf(SpringBeanFacesConverter.class)));
		assertThat(getWrapped(converter), is(instanceOf(MockFacesConverter.class)));
	}

	@Test
	public void shouldCreateConverterByClassFromSpring() throws Exception {
		this.applicationContext.registerSingleton("bean", MockConverterForClass.class);
		Application application = createWrappedApplication();
		Converter converter = application.createConverter(Example.class);
		assertThat(converter, is(instanceOf(SpringBeanConverter.class)));
		assertThat(getWrapped(converter), is(instanceOf(MockConverterForClass.class)));
	}

	@Test
	public void shouldCreateFacesConverterByClassFromSpring() throws Exception {
		this.applicationContext.registerSingleton("bean", MockFacesConverterForClass.class);
		Application application = createWrappedApplication();
		Converter converter = application.createConverter(Example.class);
		assertThat(converter, is(instanceOf(SpringBeanFacesConverter.class)));
		assertThat(getWrapped(converter), is(instanceOf(MockFacesConverterForClass.class)));
	}

	@Test
	public void shouldDelegateCreateConverterByClass() throws Exception {
		Application application = createWrappedApplication();
		Converter converter = mock(Converter.class);
		given(this.delegateApplication.createConverter(Example.class)).willReturn(converter);
		Converter actual = application.createConverter(Example.class);
		assertThat(actual, is(sameInstance(converter)));
	}

	@Test
	public void shouldFailIfMultipleConvertersRegisteredForClass() throws Exception {
		this.applicationContext.registerSingleton("bean1", MockConverterForClass.class);
		this.applicationContext.registerSingleton("bean2", MockFacesConverterForClass.class);
		Application application = createWrappedApplication();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Multiple JSF converters registered with Spring for " + Example.class.getName()
				+ " : [bean1, bean2]");
		application.createConverter(Example.class);
	}

	private Object getWrapped(Converter converter) {
		return ((FacesWrapper) converter).getWrapped();
	}

	private static class Example {
	}

	private static class MockConverter implements org.springframework.springfaces.convert.Converter<Example> {
		public Example getAsObject(FacesContext context, UIComponent component, String value) {
			return null;
		}

		public String getAsString(FacesContext context, UIComponent component, Example value) {
			return null;
		}
	}

	private static class MockFacesConverter implements Converter {
		public Object getAsObject(FacesContext context, UIComponent component, String value) {
			return null;
		}

		public String getAsString(FacesContext context, UIComponent component, Object value) {
			return null;
		}
	}

	@ForClass
	private static class MockConverterForClass extends MockConverter {
	}

	@ForClass(Example.class)
	private static class MockFacesConverterForClass extends MockFacesConverter {
	}
}
