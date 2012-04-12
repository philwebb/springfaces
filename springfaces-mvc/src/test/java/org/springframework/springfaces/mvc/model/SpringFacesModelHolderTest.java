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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
		assertNull(new SpringFacesModelHolder(null).getModel());
	}

	@Test
	public void shouldCopyModel() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		assertEquals("v", new SpringFacesModelHolder(m).getModel().get("k"));
	}

	@Test
	public void shouldHaveStaticComponentId() throws Exception {
		String componentId = this.h.getId();
		this.h.setId("madeup");
		assertEquals(componentId, this.h.getId());
	}

	@Test
	public void shouldHaveSameClientIdAsComponentId() throws Exception {
		assertEquals(this.h.getId(), this.h.getClientId());
	}

	@Test
	public void shouldBeParameterFamily() throws Exception {
		assertEquals("javax.faces.Parameter", this.h.getFamily());
	}

	@Test
	public void shouldHaveNullRenderer() throws Exception {
		assertNull(this.h.getRenderer());
	}

	@Test
	public void shouldSupportTransient() throws Exception {
		assertFalse(this.h.isTransient());
		this.h.setTransient(true);
		assertTrue(this.h.isTransient());
	}

	@Test
	public void shouldSaveAndRestoreState() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		SpringFacesModelHolder h1 = new SpringFacesModelHolder(m);
		Object state = h1.saveState(this.context);
		// Restore : Note default constructor is required
		SpringFacesModelHolder h2 = SpringFacesModelHolder.class.newInstance();
		h2.restoreState(this.context, state);
		assertEquals("v", h2.getModel().get("k"));
	}

	@Test
	public void shouldAttach() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		List<UIComponent> children = new ArrayList<UIComponent>();
		given(viewRoot.getChildren()).willReturn(children);
		SpringFacesModelHolder.attach(this.context, viewRoot, m);
		assertEquals("v", ((SpringFacesModelHolder) children.get(0)).getModel().get("k"));
	}

	@Test
	public void shouldGetModelFromNullViewRoot() throws Exception {
		assertNull(SpringFacesModelHolder.getModel(null));
	}

	@Test
	public void shouldGetModelFromEmptyViewRoot() throws Exception {
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		assertNull(SpringFacesModelHolder.getModel(viewRoot));
	}

	@Test
	public void shouldGetModelWhenAttached() throws Exception {
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		Map<String, String> m = Collections.singletonMap("k", "v");
		SpringFacesModelHolder.attach(this.context, viewRoot, m);
		assertEquals("v", SpringFacesModelHolder.getModel(viewRoot).get("k"));
	}
}
