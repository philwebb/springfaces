package org.springframework.springfaces.mvc.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

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

/**
 * Tests for {@link ImplicitMvcFacesELResolver}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class ImplicitMvcFacesELResolverTest {

	private ImplicitMvcFacesELResolver resolver = new ImplicitMvcFacesELResolver();

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private FacesContext facesContext;

	private ELContext context = new MockELContext();

	@Before
	public void setup() {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		FacesContextSetter.setCurrentInstance(facesContext);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		Map<String, Object> viewMap = new HashMap<String, Object>();
		given(viewRoot.getViewMap()).willReturn(viewMap);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldGetController() throws Exception {
		Object handler = new Object();
		given(springFacesContext.getHandler()).willReturn(handler);
		Object value = resolver.getValue(context, null, "controller");
		assertTrue(context.isPropertyResolved());
		assertSame(handler, value);
	}

	@Test
	public void shouldGetModel() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key", "value");
		SpringFacesModel.put(facesContext.getViewRoot(), model);
		Object value = resolver.getValue(context, null, "model");
		assertTrue(context.isPropertyResolved());
		assertEquals(model, value);
	}
}
