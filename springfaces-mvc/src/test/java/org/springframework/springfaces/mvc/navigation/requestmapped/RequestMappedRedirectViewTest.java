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
package org.springframework.springfaces.mvc.navigation.requestmapped;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectView;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableView;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Tests for {@link RequestMappedRedirectView}
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestMappedRedirectViewTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private RequestMappedRedirectViewContext context;

	private Object handler;

	private Method handlerMethod;

	private Map<String, Object> model = new HashMap<String, Object>();

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private String url;

	@Before
	public void setup() {
		this.handler = new Handler();
		this.handlerMethod = ReflectionUtils.findMethod(Handler.class, "method");
		given(this.request.getContextPath()).willReturn("/context");
		given(this.request.getServletPath()).willReturn("/dispatcher");
		given(this.request.getPathInfo()).willReturn("/pathinfo");
		FacesContext facesContext = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequest()).willReturn(this.request);
		given(externalContext.getResponse()).willReturn(this.response);
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNeedContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Context must not be null");
		new RequestMappedRedirectViewSpy(null, this.handler, this.handlerMethod);
	}

	@Test
	public void shoulcNeedHandler() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Handler must not be null");
		new RequestMappedRedirectViewSpy(this.context, null, this.handlerMethod);
	}

	@Test
	public void shouldNeedHandlerMethod() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("HandlerMethod must not be null");
		new RequestMappedRedirectViewSpy(this.context, this.handler, null);
	}

	@Test
	public void shouldReturnDefaultContentType() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		assertThat(view.getContentType(), is(equalTo(AbstractView.DEFAULT_CONTENT_TYPE)));
	}

	@Test
	public void shouldRenderMethodMapping() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertThat(this.url, is(equalTo("/context/dispatcher/method")));
	}

	@Test
	public void shouldRenderHandlerMapping() throws Exception {
		this.handler = new TypeMappedHandler();
		this.handlerMethod = ReflectionUtils.findMethod(TypeMappedHandler.class, "method");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertThat(this.url, is(equalTo("/context/dispatcher/type/method")));
	}

	@Test
	public void shouldRenderSpecificDispatcherPath() throws Exception {
		given(this.context.getDispatcherServletPath()).willReturn("customdispatcher");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertThat(this.url, is(equalTo("/context/customdispatcher/method")));
	}

	@Test
	public void shouldRequireMethodMapping() throws Exception {
		this.handlerMethod = ReflectionUtils.findMethod(Handler.class, "notMapped");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("The handler method must declare @RequestMapping annotation");
		view.render(this.model, this.request, this.response);
	}

	@Test
	public void shouldRequireSingleValueInMethodMapping() throws Exception {
		this.handlerMethod = ReflectionUtils.findMethod(Handler.class, "multiple");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("@RequestMapping must have a single value to be mapped to a URL");
		view.render(this.model, this.request, this.response);
	}

	@Test
	public void shouldRequireSingleValueInHandlerMapping() throws Exception {
		this.handler = new MultiTypeMappedHandler();
		this.handlerMethod = ReflectionUtils.findMethod(MultiTypeMappedHandler.class, "method");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("@RequestMapping on handler class must have a single value to be mapped to a URL");
		view.render(this.model, this.request, this.response);
	}

	@Test
	public void shouldRespectPathVariables() throws Exception {
		this.handlerMethod = ReflectionUtils.findMethod(Handler.class, "withPathVariables");
		this.model.put("one", 1);
		this.model.put("two", 2);
		this.model.put("three", 3);
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertThat(this.url, is(equalTo("/context/dispatcher/method/1/2/3")));
	}

	@Test
	public void shouldFailIfMissingPathVariable() throws Exception {
		this.handlerMethod = ReflectionUtils.findMethod(Handler.class, "withPathVariables");
		this.model.put("one", 1);
		this.model.put("three", 3);
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to find URL template variable 'two' in source model");
		view.render(this.model, this.request, this.response);
	}

	@Test
	public void shouldBookmarkMethodMapping() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		String url = view.getBookmarkUrl(this.model, this.request);
		assertThat(url, is(equalTo("/context/dispatcher/method")));
	}

	@Test
	public void shouldCreateBookmarkableRedirectViewDelegate() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectView(this.context, this.handler, this.handlerMethod);
		BookmarkableView delegatge = view.createDelegateRedirector("/url");
		assertThat(delegatge, is(instanceOf(BookmarkableRedirectView.class)));
	}

	@Test
	public void shouldRenderViaFacesContext() throws Exception {
		// FIXME test + also with two variants
	}

	private class RequestMappedRedirectViewSpy extends RequestMappedRedirectView {

		public RequestMappedRedirectViewSpy(RequestMappedRedirectViewContext context, Object handler,
				Method handlerMethod) {
			super(context, handler, handlerMethod);
		}

		@Override
		protected BookmarkableView createDelegateRedirector(final String url) {
			return new BookmarkableRedirectView(url, true) {
				@Override
				protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl,
						boolean http10Compatible) throws IOException {
					RequestMappedRedirectViewTest.this.url = targetUrl;
				}
			};
		}
	}

	@Controller
	public static class Handler {
		@RequestMapping("/method")
		public void method() {
		}

		public void notMapped() {
		}

		@RequestMapping({ "/method", "/another" })
		public void multiple() {
		}

		@RequestMapping("/method/{one}/{two}/{three}")
		public void withPathVariables() {
		}
	}

	@Controller
	@RequestMapping("/type")
	public static class TypeMappedHandler {
		@RequestMapping("/method")
		public void method() {
		}
	}

	@Controller
	@RequestMapping({ "/type", "/another" })
	public static class MultiTypeMappedHandler {
		@RequestMapping("/method")
		public void method() {
		}
	}

}
