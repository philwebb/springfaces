package org.springframework.springfaces.mvc.servlet;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * MVC {@link HandlerInterceptor} to setup and release a {@link SpringFacesContext} instance.
 * 
 * @see FacesPostbackHandler
 * @author Phillip Webb
 */
public class FacesHandlerInterceptor extends HandlerInterceptorAdapter implements ServletContextAware,
		ServletConfigAware, ApplicationListener<ContextRefreshedEvent> {

	private ServletContext servletContext;
	private ServletConfig servletConfig;
	private FacesContextFactory facesContextFactory;
	private Lifecycle lifecycle;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		onRefresh();
	}

	/**
	 * Called on context-refresh to allow use to obtain early the {@link FacesContextFactory} and {@link Lifecycle} JSF
	 * objects.
	 */
	private void onRefresh() {
		facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		String lifecycleId = servletConfig.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
		if (lifecycleId == null) {
			servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
		}
		lifecycle = lifecycleFactory.getLifecycle(lifecycleId == null ? LifecycleFactory.DEFAULT_LIFECYCLE
				: lifecycleId);
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof Postback) {
			handler = ((Postback) handler).getHandler();
		}
		new SpringFacesContextImpl(request, response, handler);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		getSpringFacesContext().release();
	}

	/**
	 * Returns the {@link SpringFacesContextImpl}.
	 * @return the {@link SpringFacesContextImpl}
	 */
	private SpringFacesContextImpl getSpringFacesContext() {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		Assert.isInstanceOf(SpringFacesContextImpl.class, springFacesContext, "Unable to access SpringFacesContext ");
		return (SpringFacesContextImpl) springFacesContext;
	}

	/**
	 * The {@link SpringFacesContext} implementation managed by {@link FacesHandlerInterceptor}.
	 */
	private class SpringFacesContextImpl extends SpringFacesContext {

		private HttpServletRequest request;
		private HttpServletResponse response;
		private Object handler;
		private ReferenceCountedFacesContext facesContext = new ReferenceCountedFacesContext();
		private boolean released;

		public SpringFacesContextImpl(HttpServletRequest request, HttpServletResponse response, Object handler) {
			this.request = request;
			this.response = response;
			this.handler = handler;
			setCurrentInstance(this);
		}

		public void release() {
			released = true;
			facesContext.releaseDelegate();
			setCurrentInstance(null);
		}

		@Override
		public Object getHandler() {
			checkNotRelased();
			return handler;
		}

		@Override
		public Lifecycle getLifecycle() {
			checkNotRelased();
			return lifecycle;
		}

		@Override
		public FacesContext getFacesContext() {
			checkNotRelased();
			facesContext.addReference();
			return facesContext;
		}

		private void checkNotRelased() {
			Assert.state(!released, "The SpringFacesContext has been release");
		}

		/**
		 * A reference counted wrapper for the {@link FacesContext} that will drop the underlying context when all
		 * referenced have been {@link #release() released}.
		 */
		private class ReferenceCountedFacesContext extends FacesContextWrapper {

			private FacesContext delegate;
			int referenceCount;

			public ReferenceCountedFacesContext() {
			}

			@Override
			public FacesContext getWrapped() {
				if (delegate == null) {
					delegate = facesContextFactory.getFacesContext(servletContext, request, response, lifecycle);
				}
				return delegate;
			}

			public void addReference() {
				referenceCount++;
			}

			@Override
			public void release() {
				referenceCount--;
				if (referenceCount == 0) {
					releaseDelegate();
				}
			}

			public void releaseDelegate() {
				if (delegate != null) {
					delegate.release();
					delegate = null;
				}
			}
		}
	}
}
