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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PreRenderComponentEvent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;

/**
 * Tests for {@link DestinationAndModel}.
 * @author Phillip Webb
 */
public class DestinationAndModelTest {

	@Rule
	public ExpectedException thown = ExpectedException.none();

	@Test
	public void shouldNotAllowNullNavigationOutcomeWithPreRenderComponentEvent() throws Exception {
		this.thown.expect(IllegalArgumentException.class);
		this.thown.expectMessage("NavigationOutcome must not be null");
		new DestinationAndModel(null, (PreRenderComponentEvent) null);
	}

	@Test
	public void shouldNotAllowNullNavigationOutcomeWithActionEvent() throws Exception {
		this.thown.expect(IllegalArgumentException.class);
		this.thown.expectMessage("NavigationOutcome must not be null");
		new DestinationAndModel(null, (ActionEvent) null);
	}

	@Test
	public void shouldGetDestinationFromNavigationOutcome() throws Exception {
		Object destination = new Object();
		NavigationOutcome outcome = new NavigationOutcome(destination);
		DestinationAndModel dam = new DestinationAndModel(outcome, (ActionEvent) null);
		assertThat(dam.getDestination(), is(sameInstance(destination)));
	}

	@Test
	public void shouldAllowNullPreRenderComponentEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		DestinationAndModel dam = new DestinationAndModel(outcome, (PreRenderComponentEvent) null);
		assertThat(dam.getComponent(), is(nullValue()));
	}

	@Test
	public void shouldObtainComponentFromPreRenderComponentEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		UIComponent component = mock(UIComponent.class);
		PreRenderComponentEvent preRenderComponentEvent = new PreRenderComponentEvent(component);
		DestinationAndModel dam = new DestinationAndModel(outcome, preRenderComponentEvent);
		assertThat(dam.getComponent(), is(sameInstance(component)));
	}

	@Test
	public void shouldAllowNullActionEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		DestinationAndModel dam = new DestinationAndModel(outcome, (ActionEvent) null);
		assertThat(dam.getComponent(), is(nullValue()));
	}

	@Test
	public void shouldObtainComponentFromActionEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		UIComponent component = mock(UIComponent.class);
		ActionEvent actionEvent = new ActionEvent(component);
		DestinationAndModel dam = new DestinationAndModel(outcome, actionEvent);
		assertThat(dam.getComponent(), is(sameInstance(component)));
	}

	@Test
	public void shouldBuildModel() throws Exception {
		Map<String, Object> implicitModel = new HashMap<String, Object>();
		implicitModel.put("implicit", "value");
		NavigationOutcome outcome = new NavigationOutcome(new Object(), implicitModel);
		UIComponent component = mock(UIComponent.class);
		ActionEvent actionEvent = new ActionEvent(component);
		final ModelBuilder modelBuilder = mock(ModelBuilder.class);
		DestinationAndModel dam = new DestinationAndModel(outcome, actionEvent) {
			@Override
			protected ModelBuilder newModelBuilder(FacesContext context) {
				return modelBuilder;
			}
		};
		FacesContext context = mock(FacesContext.class);
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("parameters", Collections.<String> emptyList());
		Map<String, Object> resolvedViewModel = Collections.<String, Object> singletonMap("resolved", "resolvedValue");
		dam.getModel(context, parameters, resolvedViewModel);
		InOrder ordered = inOrder(modelBuilder);
		ordered.verify(modelBuilder).addFromComponent(component);
		ordered.verify(modelBuilder).add(implicitModel, true);
		ordered.verify(modelBuilder).addFromParameterList(parameters);
		ordered.verify(modelBuilder).add(resolvedViewModel, false);
	}
}
