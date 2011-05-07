package org.springframework.springfaces.mvc.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.View;

public class DefaultFacesConfig implements FacesPostbackOrginalHandlerLocator, DestinationViewResolver,
		ApplicationListener<ContextRefreshedEvent> {

	private DelegateDispatcherServlet delegate = new DelegateDispatcherServlet();

	public void onApplicationEvent(ContextRefreshedEvent event) {
		delegate.onApplicationEvent(event);
	}

	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		return delegate.getHandler(request);
	}

	public View resolveDirectViewId(String viewId, Locale locale) {
		return delegate.resolveViewId(viewId, locale);
	}

	public View resolveDestination(Object destination, Locale locale) throws Exception {
		return delegate.resolveViewId(destination.toString(), locale);
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
