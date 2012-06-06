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
package org.springframework.springfaces.mvc.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link SpringFacesModelHolder}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringFacesModelHolderTest {

	private SpringFacesModelHolder h = new SpringFacesModelHolder(null);

	@Mock
	FacesContext context;

	@Test
	public void shouldSupportNullModel() throws Exception {
		assertThat(new SpringFacesModelHolder(null).getModel(), is(nullValue()));
	}

	@Test
	public void shouldCopyModel() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		assertThat(new SpringFacesModelHolder(m).getModel().get("k"), is(equalTo((Object) "v")));
	}

	@Test
	public void shouldHaveStaticComponentId() throws Exception {
		String componentId = this.h.getId();
		this.h.setId("madeup");
		assertThat(this.h.getId(), is(equalTo(componentId)));
	}

	@Test
	public void shouldHaveSameClientIdAsComponentId() throws Exception {
		assertThat(this.h.getClientId(), is(equalTo(this.h.getId())));
	}

	@Test
	public void shouldBeParameterFamily() throws Exception {
		assertThat(this.h.getFamily(), is(equalTo("javax.faces.Parameter")));
	}

	@Test
	public void shouldHaveNullRenderer() throws Exception {
		assertThat(this.h.getRenderer(), is(nullValue()));
	}

	@Test
	public void shouldSupportTransient() throws Exception {
		assertThat(this.h.isTransient(), is(false));
		this.h.setTransient(true);
		assertThat(this.h.isTransient(), is(true));
	}

	@Test
	public void shouldSaveAndRestoreState() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		SpringFacesModelHolder h1 = new SpringFacesModelHolder(m);
		Object state = h1.saveState(this.context);
		// Restore : Note default constructor is required
		SpringFacesModelHolder h2 = SpringFacesModelHolder.class.newInstance();
		h2.restoreState(this.context, state);
		assertThat(h2.getModel().get("k"), is(equalTo((Object) "v")));
	}

	@Test
	public void shouldAttach() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		List<UIComponent> children = new ArrayList<UIComponent>();
		given(viewRoot.getChildren()).willReturn(children);
		SpringFacesModelHolder.attach(this.context, viewRoot, m);
		assertThat(((SpringFacesModelHolder) children.get(0)).getModel().get("k"), is(equalTo((Object) "v")));
	}

	@Test
	public void shouldGetModelFromNullViewRoot() throws Exception {
		assertThat(SpringFacesModelHolder.getModel(null), is(nullValue()));
	}

	@Test
	public void shouldGetModelFromEmptyViewRoot() throws Exception {
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		assertThat(SpringFacesModelHolder.getModel(viewRoot), is(nullValue()));
	}

	@Test
	public void shouldGetModelWhenAttached() throws Exception {
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		Map<String, String> m = Collections.singletonMap("k", "v");
		SpringFacesModelHolder.attach(this.context, viewRoot, m);
		assertThat(SpringFacesModelHolder.getModel(viewRoot).get("k"), is(equalTo((Object) "v")));
	}
}
