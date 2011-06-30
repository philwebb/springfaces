package org.springframework.springfaces.mvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * General mocks.
 * 
 * @author Phillip Webb
 */
public class SpringFacesMocks {

	/**
	 * Mock a {@link UIViewRoot} that can work with {@link SpringFacesModelHolder}s.
	 * @return a {@link UIViewRoot} mock
	 */
	public static UIViewRoot mockUIViewRootWithModelSupport() {
		UIViewRoot uiViewRoot = mock(UIViewRoot.class);
		final List<UIComponent> children = new ArrayList<UIComponent>();
		given(uiViewRoot.getChildren()).willAnswer(new Answer<List<UIComponent>>() {
			public List<UIComponent> answer(InvocationOnMock invocation) throws Throwable {
				return children;
			}
		});
		given(uiViewRoot.findComponent(anyString())).willAnswer(new Answer<UIComponent>() {
			public UIComponent answer(InvocationOnMock invocation) throws Throwable {
				return children.isEmpty() ? null : children.get(0);
			}
		});
		return uiViewRoot;
	}

	/**
	 * Mock a {@link MethodParameter}.
	 * @param parameterType the parameter type
	 * @return a {@link MethodParameter} mock
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static MethodParameter mockMethodParameter(Class parameterType) {
		MethodParameter parameter = mock(MethodParameter.class);
		given(parameter.getParameterType()).willReturn(parameterType);
		return parameter;
	}

}
