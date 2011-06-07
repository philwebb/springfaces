package org.springframework.springfaces.mvc.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Test;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.model.SpringFacesModel;

/**
 * Tests for {@link SpringFacesModelELResolver}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelELResolverTest {

	private SpringFacesModelELResolver resolver = new SpringFacesModelELResolver();

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	private void setupFacesContext() {
		FacesContext facesContext = mock(FacesContext.class);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		Map<String, Object> viewMap = new HashMap<String, Object>();
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		given(viewRoot.getViewMap()).willReturn(viewMap);
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@Test
	public void shouldReturnNullWhenNoFacesContext() throws Exception {
		assertNull(resolver.get("key"));
	}

	@Test
	public void shouldReturnNullWhenNoModel() throws Exception {
		setupFacesContext();
		assertNull(resolver.get("key"));
	}

	@Test
	public void shouldFindFromModel() throws Exception {
		setupFacesContext();
		SpringFacesModel model = new SpringFacesModel();
		model.put("key", "value");
		SpringFacesModel.saveInViewScope(FacesContext.getCurrentInstance().getViewRoot(), model);
		assertEquals("value", resolver.get("key"));
	}
}
