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
package org.springframework.springfaces.mvc.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectViewIdResolver;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;

/**
 * Tests for {@link MvcViewResolverBeanDefinitionParser}.
 * 
 * @author Phillip Webb
 */
public class MvcViewResolverBeanDefinitionParserTest {

	@Test
	public void shouldAddWithDefaults() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadApplicationContext("<faces:mvc-view-resolver/>");
		BookmarkableRedirectViewIdResolver resolver = applicationContext
				.getBean(BookmarkableRedirectViewIdResolver.class);
		View view = resolver.resolveViewName("view", Locale.US);
		assertThat(view, is(FacesView.class));
		assertThat(((FacesView) view).getViewId(), is("/WEB-INF/pages/view.xhtml"));
	}

	@Test
	public void shouldAddWithSpecificProperties() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadApplicationContext("<faces:mvc-view-resolver prefix=\"p\" suffix=\"s\" order=\"123\"/>");
		BookmarkableRedirectViewIdResolver resolver = applicationContext
				.getBean(BookmarkableRedirectViewIdResolver.class);
		View view = resolver.resolveViewName("view", Locale.US);
		assertThat(view, is(FacesView.class));
		assertThat(((FacesView) view).getViewId(), is("pviews"));
	}
}
