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
