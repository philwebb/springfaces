package org.springframework.springfaces.mvc.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
 * 
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
		assertSame(destination, dam.getDestination());
	}

	@Test
	public void shouldAllowNullPreRenderComponentEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		DestinationAndModel dam = new DestinationAndModel(outcome, (PreRenderComponentEvent) null);
		assertNull(dam.getComponent());
	}

	@Test
	public void shouldObtainComponentFromPreRenderComponentEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		UIComponent component = mock(UIComponent.class);
		PreRenderComponentEvent preRenderComponentEvent = new PreRenderComponentEvent(component);
		DestinationAndModel dam = new DestinationAndModel(outcome, preRenderComponentEvent);
		assertSame(component, dam.getComponent());
	}

	@Test
	public void shouldAllowNullActionEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		DestinationAndModel dam = new DestinationAndModel(outcome, (ActionEvent) null);
		assertNull(dam.getComponent());
	}

	@Test
	public void shouldObtainComponentFromActionEvent() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(new Object());
		UIComponent component = mock(UIComponent.class);
		ActionEvent actionEvent = new ActionEvent(component);
		DestinationAndModel dam = new DestinationAndModel(outcome, actionEvent);
		assertSame(component, dam.getComponent());
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
