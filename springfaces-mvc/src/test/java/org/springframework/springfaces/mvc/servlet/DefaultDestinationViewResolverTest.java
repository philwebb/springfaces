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
package org.springframework.springfaces.mvc.servlet;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Locale;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Tests for {@link DefaultDestinationViewResolver}.
 * 
 * @author Phillip Webb
 */
public class DefaultDestinationViewResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldNeedDispatcher() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Dispatcher must not be null");
		new DefaultDestinationViewResolver(null);
	}

	@Test
	public void shouldDelegateToDispatcher() throws Exception {
		Dispatcher dispatcher = mock(Dispatcher.class);
		FacesContext context = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Locale locale = Locale.US;
		SpringFacesModel model = new SpringFacesModel();
		String destination = "viewName";
		View view = mock(View.class);

		given(context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequest()).willReturn(request);
		given(dispatcher.resolveViewName(destination, model, locale, request)).willReturn(view);

		DefaultDestinationViewResolver resolver = new DefaultDestinationViewResolver(dispatcher);
		ModelAndView resolved = resolver.resolveDestination(context, destination, locale, model);

		verify(dispatcher).resolveViewName(destination, model, locale, request);
		assertThat(resolved.getView(), is(view));
		assertThat(resolved.getModel().size(), is(0));
	}
}
