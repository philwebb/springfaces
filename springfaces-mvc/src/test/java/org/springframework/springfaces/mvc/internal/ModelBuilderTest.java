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
package org.springframework.springfaces.mvc.internal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIPanel;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.springfaces.mvc.model.SpringFacesModel;

/**
 * Tests for {@link ModelBuilder}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class ModelBuilderTest {

	private int generatedId;

	@Mock
	private FacesContext context;

	@Mock
	private Application application;

	private ModelBuilder modelBuilder;

	@Before
	public void setup() {
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		given(this.context.getViewRoot()).willReturn(viewRoot);
		given(viewRoot.createUniqueId()).willAnswer(new Answer<String>() {
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "j_" + ModelBuilderTest.this.generatedId++;
			}
		});
		given(this.context.getApplication()).willReturn(this.application);
		this.modelBuilder = new ModelBuilder(this.context);
	}

	@Test
	public void shouldSkipNullComponent() throws Exception {
		this.modelBuilder.addFromComponent(null);
		assertThat(this.modelBuilder.getModel().size(), is(0));
	}

	@Test
	public void shouldAddFromComponents() throws Exception {
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("p1", "v1"));
		component.getChildren().add(new UIInput());
		component.getChildren().add(newUIParameter("p2", "v2"));
		this.modelBuilder.addFromComponent(component);
		Map<String, Object> model = this.modelBuilder.getModel();
		assertThat(model.size(), is(2));
		assertThat(model.get("p1"), is(equalTo((Object) "v1")));
		assertThat(model.get("p2"), is(equalTo((Object) "v2")));
	}

	@Test
	public void shouldNotResolveElExpressionInComponent() throws Exception {
		// EL expression are resolved by the UIParameter component, need to make sure we don't resolve again to protect
		// against EL injection attacks
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("p1", "#{expression}"));
		this.modelBuilder.addFromComponent(component);
		Map<String, Object> model = this.modelBuilder.getModel();
		assertThat(model.get("p1"), is(equalTo((Object) "#{expression}")));
	}

	@Test
	public void shouldGenerateMissingParameterNames() throws Exception {
		UIComponent component = new UIPanel();
		Person person = new Person();
		component.getChildren().add(newUIParameter(null, person));
		this.modelBuilder.addFromComponent(component);
		Map<String, Object> model = this.modelBuilder.getModel();
		assertThat(model.get("person"), is(equalTo((Object) person)));

	}

	@Test
	public void shouldExpandModelReferencesInComponent() throws Exception {
		UIComponent component = new UIPanel();
		Map<String, Object> sourceModel = new HashMap<String, Object>();
		sourceModel.put("m1", "v1");
		sourceModel.put("m2", "v2");
		component.getChildren().add(newUIParameter(null, new SpringFacesModel(sourceModel)));
		this.modelBuilder.addFromComponent(component);
		assertThat(this.modelBuilder.getModel(), is(equalTo((Object) sourceModel)));
	}

	@Test
	public void shouldSkipNullUIParamValue() throws Exception {
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("p1", null));
		this.modelBuilder.addFromComponent(component);
		assertThat(this.modelBuilder.getModel().size(), is(0));
	}

	@Test
	public void shouldSkipDisabledUIParam() throws Exception {
		UIComponent component = new UIPanel();
		UIParameter p = newUIParameter("p1", "v1");
		p.setDisable(true);
		component.getChildren().add(p);
		this.modelBuilder.addFromComponent(component);
		assertThat(this.modelBuilder.getModel().size(), is(0));
	}

	@Test
	public void shouldAddFromMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "v1");
		map.put("m2", "v2");
		this.modelBuilder.add(map, false);
		assertThat(this.modelBuilder.getModel(), is(equalTo(map)));
	}

	@Test
	public void shouldSkipNullMap() throws Exception {
		this.modelBuilder.add(null, false);
		assertThat(this.modelBuilder.getModel().size(), is(0));
	}

	@Test
	public void shouldNotResolveElExpressionInMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "#{expression}");
		this.modelBuilder.add(map, false);
		assertThat(this.modelBuilder.getModel().get("m1"), is(equalTo((Object) "#{expression}")));
	}

	@Test
	public void shouldResolveElExpressionInMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "#{expression}");
		given(this.application.evaluateExpressionGet(this.context, "#{expression}", Object.class)).willReturn(
				"resolved");
		this.modelBuilder.add(map, true);
		assertThat(this.modelBuilder.getModel().get("m1"), is(equalTo((Object) "resolved")));
	}

	@Test
	public void shouldNotAttemptResolveOfMalformedExpressions() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "#{expression");
		map.put("m2", "#expression}");
		map.put("m3", "#}expression{");
		map.put("m4", new StringBuffer("#{expression}"));
		this.modelBuilder.add(map, true);
		verifyZeroInteractions(this.application);
	}

	@Test
	public void shouldAddFromParametersList() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("v1"));
		parameters.put("m2", Collections.singletonList("v2"));
		this.modelBuilder.addFromParameterList(parameters);
		Map<String, Object> model = this.modelBuilder.getModel();
		assertThat(model.size(), is(2));
		assertThat(model.get("m1"), is(equalTo((Object) "v1")));
		assertThat(model.get("m2"), is(equalTo((Object) "v2")));
	}

	@Test
	public void shouldSkipNullParametersList() throws Exception {
		this.modelBuilder.addFromParameterList(null);
		assertThat(this.modelBuilder.getModel().size(), is(0));
	}

	@Test
	public void shouldResolveElFromParametersList() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("#{expression}"));
		given(this.application.evaluateExpressionGet(this.context, "#{expression}", Object.class)).willReturn(
				"resolved");
		this.modelBuilder.addFromParameterList(parameters);
		assertThat(this.modelBuilder.getModel().get("m1"), is(equalTo((Object) "resolved")));
	}

	@Test
	public void shouldDropIfMoreThanOneParameterForKey() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("v1"));
		parameters.put("m2", Arrays.asList("v2a", "v2b"));
		this.modelBuilder.addFromParameterList(parameters);
		Map<String, Object> model = this.modelBuilder.getModel();
		assertThat(model.size(), is(1));
		assertThat(model.get("m1"), is(equalTo((Object) "v1")));
	}

	@Test
	public void shouldNotOverwriteEntries() throws Exception {
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("m1", "a1"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "b1");
		map.put("m2", "b2");

		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("c1"));
		parameters.put("m2", Collections.singletonList("c2"));
		parameters.put("m3", Collections.singletonList("c3"));

		this.modelBuilder.addFromComponent(component);
		this.modelBuilder.add(map, false);
		this.modelBuilder.addFromParameterList(parameters);

		Map<String, Object> model = this.modelBuilder.getModel();

		assertThat(model.size(), is(3));
		assertThat(model.get("m1"), is(equalTo((Object) "a1")));
		assertThat(model.get("m2"), is(equalTo((Object) "b2")));
		assertThat(model.get("m3"), is(equalTo((Object) "c3")));
	}

	private UIParameter newUIParameter(String name, Object value) {
		UIParameter parameter = new UIParameter();
		parameter.setName(name);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Used to test name generation
	 */
	private static class Person {
	}
}
