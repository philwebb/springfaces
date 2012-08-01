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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.converter.GenericFacesConverterTest.ClassWithConverter;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * Tests for {@link ConversionServiceBeanDefinitionParser}.
 * 
 * @author Phillip Webb
 */
public class ConversionServiceBeanDefinitionParserTest {

	private StaticWebApplicationContext applicationContext;

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private FacesContext facesContext;

	@Mock
	private Application application;

	@Mock
	private Converter facesConverter;

	private String source = "test";

	private TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

	private Object converted = new Object();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.applicationContext = SpringFacesMvcNamespaceHandlerTest.loadApplicationContext(new ClassPathResource(
				"testSpringFacesMvcNamespaceConverter.xml", getClass()));
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		given(this.springFacesContext.getFacesContext()).willReturn(this.facesContext);
		given(this.facesContext.getApplication()).willReturn(this.application);
		given(this.application.createConverter(ClassWithConverter.class)).willReturn(this.facesConverter);
		given(this.facesConverter.getAsObject(this.facesContext, null, this.source)).willReturn(this.converted);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldLoadConverter() throws Exception {
		FormattingConversionServiceFactoryBean bean = this.applicationContext
				.getBean(FormattingConversionServiceFactoryBean.class);
		FormattingConversionService conversionService = bean.getObject();
		TypeDescriptor targetType = TypeDescriptor.valueOf(ClassWithConverter.class);
		assertThat(conversionService.convert(this.source, this.sourceType, targetType), is(equalTo(this.converted)));
	}
}
