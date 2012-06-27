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
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import javax.el.ELContext;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.MockELContext;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * Tests for {@link ImplicitSpringFacesELResolver}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class ImplicitSpringFacesELResolverTest {

	private ImplicitSpringFacesELResolver resolver = new ImplicitSpringFacesELResolver();

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private FacesContext facesContext;

	private ELContext context = new MockELContext();

	@Before
	public void setup() {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		FacesContextSetter.setCurrentInstance(this.facesContext);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		given(this.facesContext.getViewRoot()).willReturn(viewRoot);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldGetHandler() throws Exception {
		Object handler = new Object();
		given(this.springFacesContext.getHandler()).willReturn(handler);
		Object value = this.resolver.getValue(this.context, null, "handler");
		assertThat(this.context.isPropertyResolved(), is(true));
		assertThat(value, is(sameInstance(handler)));
	}

	@Test
	public void shouldGetController() throws Exception {
		Object controller = new Object();
		given(this.springFacesContext.getController()).willReturn(controller);
		Object value = this.resolver.getValue(this.context, null, "controller");
		assertThat(this.context.isPropertyResolved(), is(true));
		assertThat(value, is(sameInstance(controller)));
	}

	@Test
	public void shouldGetModel() throws Exception {
		SpringFacesModel model = new SpringFacesModel();
		model.put("key", "value");
		SpringFacesModelHolder.attach(this.facesContext, this.facesContext.getViewRoot(), model);
		Object value = this.resolver.getValue(this.context, null, "model");
		assertThat(this.context.isPropertyResolved(), is(true));
		assertThat(value, is(equalTo((Object) model)));
	}
}
