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
package org.springframework.springfaces.mvc.servlet.view;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Tests for {@link BookmarkableRedirectViewIdResolver}.
 * 
 * @author Phillip Webb
 */
public class BookmarkableRedirectViewIdResolverTest {

	private BookmarkableRedirectViewIdResolver resolver;

	@Before
	public void setup() {
		this.resolver = new BookmarkableRedirectViewIdResolver();
		ApplicationContext context = new StaticApplicationContext();
		this.resolver.setViewClass(FacesView.class);
		this.resolver.setApplicationContext(context);
	}

	@Test
	public void shouldDefaultToJustAboveLowestOrder() throws Exception {
		assertThat(this.resolver.getOrder() + 1, is(Ordered.LOWEST_PRECEDENCE));
		assertThat(this.resolver.getOrder(), is(lessThan(new UrlBasedViewResolver().getOrder())));
	}

	@Test
	public void shouldCreateNonRedirectView() throws Exception {
		View view = this.resolver.createView("test", Locale.CANADA);
		assertThat(view, is(instanceOf(FacesView.class)));
	}

	@Test
	public void shouldCreateRedirectView() throws Exception {
		View view = this.resolver.createView("redirect:http://springsource.org", Locale.UK);
		assertThat(view, is(instanceOf(BookmarkableView.class)));
		String url = ((BookmarkableView) view).getBookmarkUrl(Collections.<String, Object> emptyMap(),
				mock(HttpServletRequest.class));
		assertThat(url, is("http://springsource.org"));
	}
}
