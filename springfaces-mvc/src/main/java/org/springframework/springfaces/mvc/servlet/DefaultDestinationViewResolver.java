package org.springframework.springfaces.mvc.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Default implementation of {@link DestinationViewResolver} that can resolves destinations to views in the same way as
 * the standard {@link DispatcherServlet}.
 * 
 * @author Phillip Webb
 */
public class DefaultDestinationViewResolver implements DestinationViewResolver,
		ApplicationListener<ContextRefreshedEvent> {

	private DelegateDispatcherServlet delegate = new DelegateDispatcherServlet();

	public void onApplicationEvent(ContextRefreshedEvent event) {
		delegate.onApplicationEvent(event);
	}

	public ModelAndView resolveDestination(Object destination, Locale locale, SpringFacesModel model) throws Exception {
		return new ModelAndView(delegate.resolveViewId(destination.toString(), locale));
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
