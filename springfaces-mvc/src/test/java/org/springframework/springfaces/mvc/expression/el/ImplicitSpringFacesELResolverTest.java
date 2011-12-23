package org.springframework.springfaces.mvc.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
 * 
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
		assertTrue(this.context.isPropertyResolved());
		assertSame(handler, value);
	}

	@Test
	public void shouldGetController() throws Exception {
		Object controller = new Object();
		given(this.springFacesContext.getController()).willReturn(controller);
		Object value = this.resolver.getValue(this.context, null, "controller");
		assertTrue(this.context.isPropertyResolved());
		assertSame(controller, value);
	}

	@Test
	public void shouldGetModel() throws Exception {
		SpringFacesModel model = new SpringFacesModel();
		model.put("key", "value");
		SpringFacesModelHolder.attach(this.facesContext, this.facesContext.getViewRoot(), model);
		Object value = this.resolver.getValue(this.context, null, "model");
		assertTrue(this.context.isPropertyResolved());
		assertEquals(model, value);
	}
}
