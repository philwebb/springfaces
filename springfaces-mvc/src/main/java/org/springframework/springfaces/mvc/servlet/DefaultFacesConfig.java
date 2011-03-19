package org.springframework.springfaces.mvc.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.View;

public class DefaultFacesConfig implements FacesPostbackOrginalHandlerLocator, ViewIdResolver,
		ApplicationListener<ContextRefreshedEvent> {

	private DelegateDispatcherServlet delegate = new DelegateDispatcherServlet();

	private String viewIdPrefix = "mvc:";

	public void onApplicationEvent(ContextRefreshedEvent event) {
		delegate.onApplicationEvent(event);
	}

	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		return delegate.getHandler(request);
	}

	public boolean isResolvable(String viewId) {
		return (viewId != null) && (viewId.startsWith(viewIdPrefix));
	}

	public View resolveViewId(String viewId, Locale locale) {
		if (isResolvable(viewId)) {
			String viewName = viewId.substring(viewIdPrefix.length());
			return delegate.resolveViewId(viewName, locale);
		}

		return null;
	}

	private static class DelegateDispatcherServlet extends DispatcherServlet {
		private static final long serialVersionUID = 1L;

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			return super.getHandler(request);
		}

		public View resolveViewId(String viewName, Locale locale) {
			try {
				return resolveViewName(viewName, null, locale, null);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
