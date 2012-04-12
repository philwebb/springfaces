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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockMethodParameter;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link NavigationContextMethodArgumentResolver}.
 * 
 * @author Phillip Webb
 */
public class NavigationContextMethodArgumentResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private NavigationContextMethodArgumentResolver resolver;

	@Mock
	private NavigationContext navigationContext;

	@Mock
	private ModelAndViewContainer mavContainer;

	@Mock
	private NativeWebRequest webRequest;

	@Mock
	private WebDataBinderFactory binderFactory;

	@Mock
	private HtmlCommandButton component;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.resolver = new NavigationContextMethodArgumentResolver(this.navigationContext);
		given(this.navigationContext.getComponent()).willReturn(this.component);
		given(this.navigationContext.getOutcome()).willReturn("outcome");
	}

	@Test
	public void shouldNeedNavigationContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("NavigationContext must not be null");
		new NavigationContextMethodArgumentResolver(null);
	}

	@Test
	public void shouldSupportNavigationContext() throws Exception {
		assertTrue(this.resolver.supportsParameter(mockMethodParameter(NavigationContext.class)));
		assertFalse(this.resolver.supportsParameter(mockMethodParameter(ExtendsNavigationContext.class)));
		assertSame(this.navigationContext, this.resolver.resolveArgument(mockMethodParameter(NavigationContext.class),
				this.mavContainer, this.webRequest, this.binderFactory));
	}

	@Test
	public void shouldSupportComponent() throws Exception {
		assertTrue(this.resolver.supportsParameter(mockMethodParameter(HtmlCommandButton.class)));
		assertTrue(this.resolver.supportsParameter(mockMethodParameter(UICommand.class)));
		assertTrue(this.resolver.supportsParameter(mockMethodParameter(UIComponent.class)));
		assertFalse(this.resolver.supportsParameter(mockMethodParameter(HtmlCommandLink.class)));
		assertSame(this.component, this.resolver.resolveArgument(mockMethodParameter(UICommand.class),
				this.mavContainer, this.webRequest, this.binderFactory));
	}

	@Test
	public void shouldSupportOutcome() throws Exception {
		assertTrue(this.resolver.supportsParameter(mockMethodParameter(String.class)));
		assertEquals("outcome", this.resolver.resolveArgument(mockMethodParameter(String.class), this.mavContainer,
				this.webRequest, this.binderFactory));
	}

	private static interface ExtendsNavigationContext extends NavigationContext {
	}
}
