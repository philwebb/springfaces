package org.springframework.springfaces.mvc;

import java.util.Locale;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.mvc.internal.MvcNavigationHandler;
import org.springframework.springfaces.mvc.internal.MvcResponseStateManager;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.mvc.view.FacesViewStateHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class SpringFacesFactories implements FacesWrapperFactory<Object>, ApplicationListener<ContextRefreshedEvent> {

	private DelegateDispatcherServlet delegateDispatcherServlet = new DelegateDispatcherServlet();
	private FacesViewStateHandler stateHandler;

	public Object newWrapper(Class<?> typeClass, Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new MvcResponseStateManager((ResponseStateManager) delegate, stateHandler);
		}
		if (delegate instanceof ViewHandler) {
			return new MvcViewHandler((ViewHandler) delegate, delegateDispatcherServlet);
		}
		if (ConfigurableNavigationHandler.class.equals(typeClass)) {
			return new MvcNavigationHandler((ConfigurableNavigationHandler) delegate);
		}
		return null;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		delegateDispatcherServlet.onApplicationEvent(event);
	}

	public void setStateHandler(FacesViewStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}

	private static class DelegateDispatcherServlet extends DispatcherServlet implements ViewResolver {
		private static final long serialVersionUID = 1L;

		public View resolveViewName(String viewName, Locale locale) throws Exception {
			return resolveViewName(viewName, null, locale, null);
		}
	}
}
