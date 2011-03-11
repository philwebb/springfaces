package org.springframework.springfaces;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.support.WebContentGenerator;

public class XFacesHandlerAdapter extends WebContentGenerator implements HandlerAdapter, Ordered, BeanFactoryAware,
		ServletConfigAware, ApplicationListener<ContextRefreshedEvent> {

	private BeanFactory beanFactory;

	private AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

	private ServletConfig servletConfig;

	private FacesContextFactory facesContextFactory;

	private Lifecycle lifecycle;

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
		//		return annotationMethodHandlerAdapter == null ? Ordered.LOWEST_PRECEDENCE : annotationMethodHandlerAdapter
		//				.getOrder() - 1;
	}

	public boolean supports(Object handler) {
		return (this.annotationMethodHandlerAdapter != null) && (this.annotationMethodHandlerAdapter.supports(handler));
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// Acquire the FacesContext instance for this request
		FacesContext context = facesContextFactory.getFacesContext(servletConfig.getServletContext(), request,
				response, lifecycle);

		// Execute the request processing lifecycle for this request
		try {
			if (context.isPostback()) {
				lifecycle.execute(context);
				lifecycle.render(context);
			} else {
				return annotationMethodHandlerAdapter.handle(request, response, handler);
			}
			return null;
		} catch (FacesException e) {
			Throwable t = e.getCause();
			if (t == null) {
				throw new ServletException(e.getMessage(), e);
			} else {
				if (t instanceof ServletException) {
					throw ((ServletException) t);
				} else if (t instanceof IOException) {
					throw ((IOException) t);
				} else {
					throw new ServletException(t.getMessage(), t);
				}
			}
		}
	}

	/**
	 * <p>Acquire the factory instances we will require.</p>
	 *
	 * @throws ServletException if, for any reason, the startup of
	 * this Faces application failed.  This includes errors in the
	 * config file that is parsed before or during the processing of
	 * this <code>init()</code> method.
	 */
	private void init() throws ServletException {

		// Save our ServletConfig instance

		// Acquire our FacesContextFactory instance
		try {
			facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		} catch (FacesException e) {
			//            ResourceBundle rb = LOGGER.getResourceBundle();
			//            String msg = rb.getString("severe.webapp.facesservlet.init_failed");
			//            Throwable rootCause = (e.getCause() != null) ? e.getCause() : e;
			//            LOGGER.log(Level.SEVERE, msg, rootCause);
			throw new UnavailableException("");
		}

		// Acquire our Lifecycle instance
		try {
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			String lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
			lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
		} catch (FacesException e) {
			Throwable rootCause = e.getCause();
			if (rootCause == null) {
				throw e;
			} else {
				throw new ServletException(e.getMessage(), rootCause);
			}
		}

	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return annotationMethodHandlerAdapter.getLastModified(request, handler);
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		initAnnotationMethodHandlerAdapter(event.getApplicationContext());
		try {
			init();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void initAnnotationMethodHandlerAdapter(ApplicationContext context) {
		if (this.annotationMethodHandlerAdapter == null) {
			this.annotationMethodHandlerAdapter = context.getBean(AnnotationMethodHandlerAdapter.class);
		}
	}

	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}
}
