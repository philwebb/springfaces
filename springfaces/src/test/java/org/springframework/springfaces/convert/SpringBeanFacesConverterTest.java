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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringBeanFacesConverter}.
 * @author Phillip Webb
 */
public class SpringBeanFacesConverterTest {

	@Mock
	private FacesContext context;

	@Mock
	private WebApplicationContext applicationContext;

	private String beanName = "bean";

	@Mock
	private Converter bean;

	@Mock
	private UIComponent component;

	private SpringBeanFacesConverter converter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(this.context, this.applicationContext);
		given(this.applicationContext.getBean(this.beanName)).willReturn(this.bean);
		this.converter = new SpringBeanFacesConverter(this.context, this.beanName);
	}

	@Test
	public void shouldDelegateGetAsString() throws Exception {
		String value = "value";
		Object object = new Object();
		given(this.bean.getAsObject(this.context, this.component, value)).willReturn(object);
		Object actual = this.converter.getAsObject(this.context, this.component, value);
		assertThat(actual, is(object));
	}

	@Test
	public void shouldDelegateGetAsObject() throws Exception {
		Object value = new Object();
		String string = "string";
		given(this.bean.getAsString(this.context, this.component, value)).willReturn(string);
		String actual = this.converter.getAsString(this.context, this.component, value);
		assertThat(actual, is(string));
	}
}
