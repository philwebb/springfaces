package org.springframework.springfaces.mvc.internal;

import static org.junit.Assert.assertEquals;
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
import org.springframework.springfaces.mvc.model.Model;

/**
 * Tests for {@link ModelBuilder}.
 * 
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
		given(context.getViewRoot()).willReturn(viewRoot);
		given(viewRoot.createUniqueId()).willAnswer(new Answer<String>() {
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "j_" + generatedId++;
			}
		});
		given(context.getApplication()).willReturn(application);
		modelBuilder = new ModelBuilder(context);
	}

	@Test
	public void shouldSkipNullComponent() throws Exception {
		modelBuilder.addFromComponent(null);
		assertEquals(0, modelBuilder.getModel().size());
	}

	@Test
	public void shouldAddFromComponents() throws Exception {
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("p1", "v1"));
		component.getChildren().add(new UIInput());
		component.getChildren().add(newUIParameter("p2", "v2"));
		modelBuilder.addFromComponent(component);
		Map<String, Object> model = modelBuilder.getModel();
		assertEquals(2, model.size());
		assertEquals("v1", model.get("p1"));
		assertEquals("v2", model.get("p2"));
	}

	@Test
	public void shouldNotResolveElExpressionInComponent() throws Exception {
		// EL expression are resolved by the UIParameter component, need to make sure we don't resolve again to protect
		// against EL injection attacks
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("p1", "#{expression}"));
		modelBuilder.addFromComponent(component);
		Map<String, Object> model = modelBuilder.getModel();
		assertEquals("#{expression}", model.get("p1"));
	}

	@Test
	public void shouldGenerateMissingParameterNames() throws Exception {
		UIComponent component = new UIPanel();
		Person person = new Person();
		component.getChildren().add(newUIParameter(null, person));
		modelBuilder.addFromComponent(component);
		Map<String, Object> model = modelBuilder.getModel();
		assertEquals(person, model.get("person"));

	}

	@Test
	public void shouldExpandModelReferencesInComponent() throws Exception {
		UIComponent component = new UIPanel();
		Map<String, Object> sourceModel = new HashMap<String, Object>();
		sourceModel.put("m1", "v1");
		sourceModel.put("m2", "v2");
		component.getChildren().add(newUIParameter(null, new Model(sourceModel)));
		modelBuilder.addFromComponent(component);
		assertEquals(sourceModel, modelBuilder.getModel());
	}

	@Test
	public void shouldSkipNullUIParamValue() throws Exception {
		UIComponent component = new UIPanel();
		component.getChildren().add(newUIParameter("p1", null));
		modelBuilder.addFromComponent(component);
		assertEquals(0, modelBuilder.getModel().size());
	}

	@Test
	public void shouldSkipDisabledUIParam() throws Exception {
		UIComponent component = new UIPanel();
		UIParameter p = newUIParameter("p1", "v1");
		p.setDisable(true);
		component.getChildren().add(p);
		modelBuilder.addFromComponent(component);
		assertEquals(0, modelBuilder.getModel().size());
	}

	@Test
	public void shouldAddFromMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "v1");
		map.put("m2", "v2");
		modelBuilder.add(map, false);
		assertEquals(map, modelBuilder.getModel());
	}

	@Test
	public void shouldSkipNullMap() throws Exception {
		modelBuilder.add(null, false);
		assertEquals(0, modelBuilder.getModel().size());
	}

	@Test
	public void shouldNotResolveElExpressionInMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "#{expression}");
		modelBuilder.add(map, false);
		assertEquals("#{expression}", modelBuilder.getModel().get("m1"));
	}

	@Test
	public void shouldResolveElExpressionInMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "#{expression}");
		given(application.evaluateExpressionGet(context, "#{expression}", Object.class)).willReturn("resolved");
		modelBuilder.add(map, true);
		assertEquals("resolved", modelBuilder.getModel().get("m1"));
	}

	@Test
	public void shouldNotAttemptResolveOfMalformedExpressions() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m1", "#{expression");
		map.put("m2", "#expression}");
		map.put("m3", "#}expression{");
		map.put("m4", new StringBuffer("#{expression}"));
		modelBuilder.add(map, true);
		verifyZeroInteractions(application);
	}

	@Test
	public void shouldAddFromParametersList() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("v1"));
		parameters.put("m2", Collections.singletonList("v2"));
		modelBuilder.addFromParamterList(parameters);
		Map<String, Object> model = modelBuilder.getModel();
		assertEquals(2, model.size());
		assertEquals("v1", model.get("m1"));
		assertEquals("v2", model.get("m2"));
	}

	@Test
	public void shouldSkipNullParametersList() throws Exception {
		modelBuilder.addFromParamterList(null);
		assertEquals(0, modelBuilder.getModel().size());
	}

	@Test
	public void shouldResolveElFromParametersList() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("#{expression}"));
		given(application.evaluateExpressionGet(context, "#{expression}", Object.class)).willReturn("resolved");
		modelBuilder.addFromParamterList(parameters);
		assertEquals("resolved", modelBuilder.getModel().get("m1"));
	}

	@Test
	public void shouldDropIfMoreThanOneParameterForKey() throws Exception {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("m1", Collections.singletonList("v1"));
		parameters.put("m2", Arrays.asList("v2a", "v2b"));
		modelBuilder.addFromParamterList(parameters);
		Map<String, Object> model = modelBuilder.getModel();
		assertEquals(1, model.size());
		assertEquals("v1", model.get("m1"));
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

		modelBuilder.addFromComponent(component);
		modelBuilder.add(map, false);
		modelBuilder.addFromParamterList(parameters);

		Map<String, Object> model = modelBuilder.getModel();

		assertEquals(3, model.size());
		assertEquals("a1", model.get("m1"));
		assertEquals("b2", model.get("m2"));
		assertEquals("c3", model.get("m3"));
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
