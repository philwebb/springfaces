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
package org.springframework.springfaces.mvc.navigation.annotation.support;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockMethodParameter;

import org.junit.Test;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link NavigationMethodReturnValueHandler}.
 * 
 * @author Phillip Webb
 */
public class NavigationMethodReturnValueHandlerTest {

	private NavigationMethodReturnValueHandler handler = new NavigationMethodReturnValueHandler();

	@Test
	public void shouldSupportAllTypes() throws Exception {
		assertTrue(this.handler.supportsReturnType(mockMethodParameter(Object.class)));
		assertTrue(this.handler.supportsReturnType(mockMethodParameter(String.class)));
	}

	@Test
	public void shouldSetReturnValueToView() throws Exception {
		Object returnValue = new Object();
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		this.handler.handleReturnValue(returnValue, mockMethodParameter(Object.class), mavContainer,
				mock(NativeWebRequest.class));
		verify(mavContainer).setView(returnValue);
		verifyNoMoreInteractions(mavContainer);
	}
}
