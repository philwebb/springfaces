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
package org.springframework.springfaces.mvc.expression.el;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Test;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * Tests for {@link SpringFacesModelELResolver}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelELResolverTest {

	private SpringFacesModelELResolver resolver = new SpringFacesModelELResolver();

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	private void setupFacesContext() {
		FacesContext facesContext = mock(FacesContext.class);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@Test
	public void shouldReturnNullWhenNoFacesContext() throws Exception {
		assertThat(this.resolver.get("key"), is(nullValue()));
	}

	@Test
	public void shouldReturnNullWhenNoModel() throws Exception {
		setupFacesContext();
		assertThat(this.resolver.get("key"), is(nullValue()));
	}

	@Test
	public void shouldFindFromModel() throws Exception {
		setupFacesContext();
		SpringFacesModel model = new SpringFacesModel();
		model.put("key", "value");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		SpringFacesModelHolder.attach(facesContext, facesContext.getViewRoot(), model);
		assertThat(this.resolver.get("key"), is(equalTo((Object) "value")));
	}
}
