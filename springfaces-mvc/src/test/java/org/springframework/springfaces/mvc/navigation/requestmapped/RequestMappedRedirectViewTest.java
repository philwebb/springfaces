package org.springframework.springfaces.mvc.navigation.requestmapped;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
		assertEquals(AbstractView.DEFAULT_CONTENT_TYPE, view.getContentType());
	}

	@Test
	public void shouldRenderMethodMapping() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertEquals("/context/dispatcher/method", this.url);
	}

	@Test
	public void shouldRenderHandlerMapping() throws Exception {
		this.handler = new TypeMappedHandler();
		this.handlerMethod = ReflectionUtils.findMethod(TypeMappedHandler.class, "method");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertEquals("/context/dispatcher/type/method", this.url);
	}

	@Test
	public void shouldRenderSpecificDispatcherPath() throws Exception {
		given(this.context.getDispatcherServletPath()).willReturn("customdispatcher");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(this.context, this.handler,
				this.handlerMethod);
		view.render(this.model, this.request, this.response);
		assertEquals("/context/customdispatcher/method", this.url);
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
		assertEquals("/context/dispatcher/method/1/2/3", this.url);
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
		assertEquals("/context/dispatcher/method", url);
	}

	@Test
	public void shouldCreateBookmarkableRedirectViewDelegate() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectView(this.context, this.handler, this.handlerMethod);
		BookmarkableView delegatge = view.createDelegateRedirector("/url");
		assertTrue(delegatge instanceof BookmarkableRedirectView);
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
