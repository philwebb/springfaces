package org.springframework.springfaces.mvc.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;

/**
 * Default implementation of {@link OriginalHandlerLocator}.
 * 
 * @author Phillip Webb
 */
public class DefaultOriginalHandlerLocator implements OriginalHandlerLocator,
		ApplicationListener<ContextRefreshedEvent> {

	private DelegateDispatcherServlet delegate = new DelegateDispatcherServlet();

	public void onApplicationEvent(ContextRefreshedEvent event) {
		delegate.onApplicationEvent(event);
	}

	public HandlerExecutionChain getOriginalHandler(HttpServletRequest request) throws Exception {
		return delegate.getHandler(request);
	}

	private static class DelegateDispatcherServlet extends DispatcherServlet {
		private static final long serialVersionUID = 1L;

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			return super.getHandler(request);
		}
	}

}
