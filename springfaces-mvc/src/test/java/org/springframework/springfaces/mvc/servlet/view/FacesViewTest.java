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
package org.springframework.springfaces.mvc.servlet.view;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.validation.BindingResult;

/**
 * Tests for {@link FacesView}.
 * 
 * @author Phillip Webb
 */
public class FacesViewTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Captor
	private ArgumentCaptor<Map<String, Object>> modelCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldCreateWithUrl() throws Exception {
		String url = "http://localhost:8080";
		FacesView view = new FacesView(url);
		assertThat(view.getUrl(), is(url));
	}

	@Test
	public void shouldNeedUrlOnCreate() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("URL must not be empty");
		new FacesView("");
	}

	@Test
	public void shouldCreateWithViewArtifact() throws Exception {
		String artifact = "/WEB-INF/pages/page.xhtml";
		ViewArtifact viewArtifact = new ViewArtifact(artifact);
		FacesView view = new FacesView(viewArtifact);
		assertThat(view.getUrl(), is(artifact));
	}

	@Test
	public void shouldNeedViewArtifact() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ViewArtifact must not be null");
		new FacesView((ViewArtifact) null);
	}

	@Test
	public void shouldRenderRemovingBindingResults() throws Exception {
		FacesView view = new FacesView();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key", "value");
		model.put("binding", mock(BindingResult.class));
		view.renderMergedOutputModel(model, this.request, this.response);
		verify(this.springFacesContext).render(eq(view), this.modelCaptor.capture());
		assertThat(this.modelCaptor.getValue().size(), is(1));
		assertThat(this.modelCaptor.getValue().get("key"), is((Object) "value"));
	}

	@Test
	public void shouldNotSetNullUrl() throws Exception {
		FacesView view = new FacesView();
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("URL must not be empty");
		view.setUrl(null);
	}

	@Test
	public void shouldGetViewArtifactFromUrl() throws Exception {
		FacesView view = new FacesView();
		view.setUrl("artifact");
		assertThat(view.getViewArtifact(), is(equalTo(new ViewArtifact("artifact"))));
	}
}
