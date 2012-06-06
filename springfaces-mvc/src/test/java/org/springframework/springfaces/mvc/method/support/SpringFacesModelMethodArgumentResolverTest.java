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
package org.springframework.springfaces.mvc.method.support;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

/**
 * Tests for {@link SpringFacesModelMethodArgumentResolver}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelMethodArgumentResolverTest {

	private SpringFacesModelMethodArgumentResolver resolver = new SpringFacesModelMethodArgumentResolver();

	private SpringFacesModel model;

	@Mock
	private FacesContext facesContext;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		given(this.facesContext.getViewRoot()).willReturn(viewRoot);
		this.model = SpringFacesModelHolder.attach(this.facesContext, viewRoot, new HashMap<String, Object>());
		FacesContextSetter.setCurrentInstance(this.facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldSupportSpringFacesModel() throws Exception {
		MethodParameter parameter = mockMethodParameter(SpringFacesModel.class);
		assertThat(this.resolver.supportsParameter(parameter), is(true));
		assertSame(this.model, this.resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportExtendedModelMap() throws Exception {
		MethodParameter parameter = mockMethodParameter(ExtendedModelMap.class);
		assertThat(this.resolver.supportsParameter(parameter), is(true));
		assertSame(this.model, this.resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportModelMap() throws Exception {
		MethodParameter parameter = mockMethodParameter(ModelMap.class);
		assertThat(this.resolver.supportsParameter(parameter), is(true));
		assertSame(this.model, this.resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportModel() throws Exception {
		MethodParameter parameter = mockMethodParameter(Model.class);
		assertThat(this.resolver.supportsParameter(parameter), is(true));
		assertSame(this.model, this.resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportMap() throws Exception {
		MethodParameter parameter = mockMethodParameter(Map.class);
		assertThat(this.resolver.supportsParameter(parameter), is(true));
		assertSame(this.model, this.resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldNotResolveWithoutFacesContext() throws Exception {
		FacesContextSetter.setCurrentInstance(null);
		MethodParameter parameter = mockMethodParameter(Map.class);
		assertFalse(this.resolver.supportsParameter(parameter));
	}

	@Test
	public void shouldResolveSingleModelValue() throws Exception {
		ComplexType v = new ComplexType();
		this.model.put("k", v);
		MethodParameter parameter = mockMethodParameter(ComplexType.class);
		assertThat(this.resolver.supportsParameter(parameter), is(true));
		assertSame(v, this.resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldNotResolveIfMultipleValues() throws Exception {
		ComplexType v1 = new ComplexType();
		ComplexType v2 = new ComplexType();
		this.model.put("k1", v1);
		this.model.put("k2", v2);
		MethodParameter parameter = mockMethodParameter(ComplexType.class);
		assertFalse(this.resolver.supportsParameter(parameter));
	}

	@Test
	public void shouldNotResolveSimpleTypes() throws Exception {
		this.model.put("k", "v");
		MethodParameter parameter = mockMethodParameter(String.class);
		assertFalse(this.resolver.supportsParameter(parameter));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MethodParameter mockMethodParameter(Class parameterType) {
		MethodParameter methodParameter = mock(MethodParameter.class);
		given(methodParameter.getParameterType()).willReturn(parameterType);
		return methodParameter;
	}

	private static class ComplexType {
	}

}
