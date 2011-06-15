package org.springframework.springfaces.mvc.navigation.requestmapped;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

	private RequestMappedRedirectViewContext context = new RequestMappedRedirectViewContext();

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
		given(request.getContextPath()).willReturn("/context");
		given(request.getServletPath()).willReturn("/dispatcher");
		given(request.getPathInfo()).willReturn("/pathinfo");
	}

	@Test
	public void shouldNeedContext() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Context must not be null");
		new RequestMappedRedirectViewSpy(null, handler, handlerMethod);
	}

	@Test
	public void shoulcNeedHandler() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Handler must not be null");
		new RequestMappedRedirectViewSpy(context, null, handlerMethod);
	}

	@Test
	public void shouldNeedHandlerMethod() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("HandlerMethod must not be null");
		new RequestMappedRedirectViewSpy(context, handler, null);
	}

	@Test
	public void shouldReturnDefaultContentType() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		assertEquals(AbstractView.DEFAULT_CONTENT_TYPE, view.getContentType());
	}

	@Test
	public void shouldRenderMethodMapping() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		view.render(model, request, response);
		assertEquals("/dispatcher/method", url);
	}

	@Test
	public void shouldRenderHandlerMapping() throws Exception {
		handler = new TypeMappedHandler();
		handlerMethod = ReflectionUtils.findMethod(TypeMappedHandler.class, "method");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		view.render(model, request, response);
		assertEquals("/dispatcher/type/method", url);
	}

	@Test
	public void shouldRenderSpecificDispatcherPath() throws Exception {
		context.setDispatcherServletPath("customdispatcher");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		view.render(model, request, response);
		assertEquals("/customdispatcher/method", url);
	}

	@Test
	public void shouldRequireMethodMapping() throws Exception {
		handlerMethod = ReflectionUtils.findMethod(Handler.class, "notMapped");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("The handler method must declare @RequestMapping annotation");
		view.render(model, request, response);
	}

	@Test
	public void shouldRequireSingleValueInMethodMapping() throws Exception {
		handlerMethod = ReflectionUtils.findMethod(Handler.class, "multiple");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("@RequestMapping must have a single value to be mapped to a URL");
		view.render(model, request, response);
	}

	@Test
	public void shouldRequireSingleValueInHandlerMapping() throws Exception {
		handler = new MultiTypeMappedHandler();
		handlerMethod = ReflectionUtils.findMethod(MultiTypeMappedHandler.class, "method");
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("@RequestMapping on handler class must have a single value to be mapped to a URL");
		view.render(model, request, response);
	}

	@Test
	public void shouldBookmarkMethodMapping() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectViewSpy(context, handler, handlerMethod);
		String url = view.getBookmarkUrl(model, request);
		assertEquals("/dispatcher/method", url);
		assertEquals(this.url, url);
	}

	@Test
	public void shouldCreateBookmarkableRedirectViewDelegate() throws Exception {
		RequestMappedRedirectView view = new RequestMappedRedirectView(context, handler, handlerMethod);
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
			RequestMappedRedirectViewTest.this.url = url;
			return new BookmarkableView() {
				public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
						throws Exception {
				}

				public String getContentType() {
					return null;
				}

				public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws Exception {
					return url;
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
