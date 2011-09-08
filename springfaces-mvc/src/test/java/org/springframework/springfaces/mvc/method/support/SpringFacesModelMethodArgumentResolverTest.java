package org.springframework.springfaces.mvc.method.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

/**
 * Tests for {@link SpringFacesModelMethodArgumentResolver}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelMethodArgumentResolverTest {

	private SpringFacesModelMethodArgumentResolver resolver = new SpringFacesModelMethodArgumentResolver();

	private SpringFacesModel model;

	@Mock
	private FacesContext facesContext;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		model = SpringFacesModelHolder.attach(facesContext, viewRoot, new HashMap<String, Object>());
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldSupportSpringFacesModel() throws Exception {
		MethodParameter parameter = mockMethodParameter(SpringFacesModel.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(model, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportExtendedModelMap() throws Exception {
		MethodParameter parameter = mockMethodParameter(ExtendedModelMap.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(model, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportModelMap() throws Exception {
		MethodParameter parameter = mockMethodParameter(ModelMap.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(model, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportModel() throws Exception {
		MethodParameter parameter = mockMethodParameter(Model.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(model, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldSupportMap() throws Exception {
		MethodParameter parameter = mockMethodParameter(Map.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(model, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldNotResolveWithoutFacesContext() throws Exception {
		FacesContextSetter.setCurrentInstance(null);
		MethodParameter parameter = mockMethodParameter(Map.class);
		assertFalse(resolver.supportsParameter(parameter));
	}

	@Test
	public void shouldResolveSingleModelValue() throws Exception {
		ComplexType v = new ComplexType();
		model.put("k", v);
		MethodParameter parameter = mockMethodParameter(ComplexType.class);
		assertTrue(resolver.supportsParameter(parameter));
		assertSame(v, resolver.resolveArgument(parameter, null, null, null));
	}

	@Test
	public void shouldNotResolveIfMultipleValues() throws Exception {
		ComplexType v1 = new ComplexType();
		ComplexType v2 = new ComplexType();
		model.put("k1", v1);
		model.put("k2", v2);
		MethodParameter parameter = mockMethodParameter(ComplexType.class);
		assertFalse(resolver.supportsParameter(parameter));
	}

	@Test
	public void shouldNotResolveSimpleTypes() throws Exception {
		model.put("k", "v");
		MethodParameter parameter = mockMethodParameter(String.class);
		assertFalse(resolver.supportsParameter(parameter));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MethodParameter mockMethodParameter(Class parameterType) {
		MethodParameter methodParameter = mock(MethodParameter.class);
		given(methodParameter.getParameterType()).willReturn(parameterType);
		return methodParameter;
	}

	private static class ComplexType {
	}

}
