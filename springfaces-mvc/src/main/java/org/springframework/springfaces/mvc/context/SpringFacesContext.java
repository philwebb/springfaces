package org.springframework.springfaces.mvc.context;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.webapp.FacesServlet;

import org.springframework.core.NamedThreadLocal;
import org.springframework.springfaces.mvc.servlet.FacesHandlerInterceptor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Context for a single MVC request that may result in a JSF response. A {@link SpringFacesContext} instance is
 * associated with a Spring MVC request from the point that the {@link HandlerMapping#getHandler handler} is obtained.
 * The instance remains active and bound to the current thread until the MVC request has completed.
 * <p>
 * The {@link SpringFacesContext} can be used to access the JSF {@link #getLifecycle() lifecycle} and
 * {@link #getFacesContext(boolean) facesContext} regardless of the fact that the request is not being processed by the
 * {@link FacesServlet}.
 * 
 * @see #getCurrentInstance()
 * @see FacesHandlerInterceptor
 * @author Phillip Webb
 */
public abstract class SpringFacesContext {

	private static ThreadLocal<SpringFacesContext> instance = new NamedThreadLocal<SpringFacesContext>(
			"Spring Faces Context");

	/**
	 * Returns a JSF {@link Lifecycle} that can be used to {@link Lifecycle#execute execute} and
	 * {@link Lifecycle#render} a JSF response.
	 * @return The JSF {@link Lifecycle}
	 */
	public abstract Lifecycle getLifecycle();

	/**
	 * Returns a JSF {@link FacesContext} for the current request. <b>NOTE:</b> Any {@link FacesContext} successfully
	 * obtained using this method should be {@link FacesContext#release() released} after use. <code>
	 * FacesContext context = springFacesContext.getFacesContext(true);
	 * try {
	 *   // work with context
	 * } finally {
	 *   context.release();
	 * }
	 * </code>
	 * @return A {@link FacesContext} instance
	 */
	public abstract FacesContext getFacesContext();

	/**
	 * Returns the MVC Handler chosen by the {@link DispatcherServlet}.
	 * @return The MVC handler
	 */
	public abstract Object getHandler();

	/**
	 * Returns the current {@link SpringFacesContext} instance or <tt>null</tt> the context is unavailable.
	 * @return The {@link SpringFacesContext} or <tt>null</tt>
	 */
	public static SpringFacesContext getCurrentInstance() {
		return getCurrentInstance(false);
	}

	/**
	 * Returns the current {@link SpringFacesContext}.
	 * @param required <tt>true</tt> if an {@link IllegalStateException} should be thrown when the context cannot be
	 * obtained. <tt>false</tt> if <tt>null</tt> should be returned when the context cannot be obtained.
	 * @return The {@link SpringFacesContext} (or <tt>null</tt> if <tt>required</tt> is <tt>false</tt>)
	 */
	public static SpringFacesContext getCurrentInstance(boolean required) {
		SpringFacesContext context = instance.get();
		if (context == null && required) {
			throw new IllegalStateException("Unable to obtain the SpringFacesContext, perhaps "
					+ FacesHandlerInterceptor.class.getSimpleName() + " has not been registered with Spring");
		}
		return context;
	}

	/**
	 * Protected method that should be used by subclasses to set the current context instance.
	 * @param context The context instance to set or <tt>null</tt> to remove the current instance
	 */
	protected static void setCurrentInstance(SpringFacesContext context) {
		if (context == null) {
			instance.remove();
		} else {
			instance.set(context);
		}
	}
}
