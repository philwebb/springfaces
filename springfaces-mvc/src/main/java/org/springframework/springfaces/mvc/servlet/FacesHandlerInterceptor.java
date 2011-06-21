package org.springframework.springfaces.mvc.servlet;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * MVC {@link HandlerInterceptor} to setup and release a {@link SpringFacesContext} instance.
 * 
 * @see FacesPostbackHandler
 * @see Postback
 * @author Phillip Webb
 */
public class FacesHandlerInterceptor extends HandlerInterceptorAdapter implements ServletContextAware {

	private ServletContext servletContext;
	private FacesContextFactory facesContextFactory;
	private Lifecycle lifecycle;
	private String lifecycleId;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		obtainFacesObjects();
		if (handler instanceof Postback) {
			handler = ((Postback) handler).getHandler();
		}
		new SpringFacesContextImpl(request, response, handler);
		return true;
	}

	/**
	 * Obtain any JSF objects that have not yet been acquired.
	 */
	private void obtainFacesObjects() {
		if (facesContextFactory == null) {
			facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		}
		if (lifecycle == null) {
			String lifecycleIdToUse = lifecycleId;
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			if (lifecycleIdToUse == null) {
				lifecycleIdToUse = servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
			}
			if (lifecycleIdToUse == null) {
				lifecycleIdToUse = LifecycleFactory.DEFAULT_LIFECYCLE;
			}
			lifecycle = lifecycleFactory.getLifecycle(lifecycleIdToUse);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		SpringFacesContextImpl context = getSpringFacesContext(false);
		if (context != null) {
			context.release();
		}
	}

	/**
	 * Returns the {@link SpringFacesContextImpl}.
	 * @return the {@link SpringFacesContextImpl}
	 */
	private SpringFacesContextImpl getSpringFacesContext(boolean required) {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance(required);
		if (springFacesContext == null) {
			return null;
		}
		Assert.isInstanceOf(SpringFacesContextImpl.class, springFacesContext, "Unable to access SpringFacesContext ");
		return (SpringFacesContextImpl) springFacesContext;
	}

	/**
	 * Set the lifecycle identifier to use when {@link LifecycleFactory#getLifecycle(String) creating} the JSF
	 * {@link Lifecycle}. When not specified the <tt>javax.faces.LIFECYCLE_ID</tt> initiation parameter of the
	 * {@link DispatcherServlet} will be used. If no explicit initialization parameter is set the
	 * {@link LifecycleFactory#DEFAULT_LIFECYCLE default} lifecycle identifier will be used.
	 * @param lifecycleId The lifecycle id or <tt>null</tt>
	 */
	public void setLifecycleId(String lifecycleId) {
		this.lifecycleId = lifecycleId;
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
		private WebApplicationContext webApplicationContext;
		private ModelAndViewArtifact rendering;

		public SpringFacesContextImpl(HttpServletRequest request, HttpServletResponse response, Object handler) {
			this.request = request;
			this.response = response;
			this.handler = handler;
			this.webApplicationContext = RequestContextUtils.getWebApplicationContext(request, servletContext);
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
		public Object getController() {
			Object controller = getHandler();
			if ((controller != null) && (controller instanceof HandlerMethod)) {
				controller = ((HandlerMethod) controller).createWithResolvedBean().getBean();
			}
			return controller;
		}

		@Override
		public FacesContext getFacesContext() {
			checkNotRelased();
			facesContext.addReference();
			return facesContext;
		}

		@Override
		public WebApplicationContext getWebApplicationContext() {
			return webApplicationContext;
		}

		@Override
		public void render(ModelAndViewArtifact modelAndViewArtifact) {
			checkNotRelased();
			Assert.state(rendering == null, "The SpringFacesContext is already rendering");
			this.rendering = modelAndViewArtifact;
			try {
				FacesContext facesContext = getFacesContext();
				try {
					lifecycle.execute(facesContext);
					lifecycle.render(facesContext);
				} finally {
					facesContext.release();
				}
			} finally {
				this.rendering = null;
			}
		}

		@Override
		public ModelAndViewArtifact getRendering() {
			return rendering;
		}

		private void checkNotRelased() {
			Assert.state(!released, "The SpringFacesContext has been released");
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
