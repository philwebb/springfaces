package org.springframework.springfaces.mvc.context;

import javax.faces.context.FacesContext;
import javax.faces.webapp.FacesServlet;

import org.springframework.core.NamedThreadLocal;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.servlet.FacesHandlerInterceptor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Context for a single MVC request that may result in a JSF response. A {@link SpringFacesContext} instance is
 * associated with a Spring MVC request from the point that the {@link HandlerMapping#getHandler handler} is obtained.
 * The instance remains active and bound to the current thread until the MVC request has completed.
 * <p>
 * The {@link SpringFacesContext} can be used to access the JSF {@link #getFacesContext facesContext} regardless of the
 * fact that the request is not being processed by the {@link FacesServlet}.
 * 
 * @see #getCurrentInstance()
 * @see FacesHandlerInterceptor
 * 
 * @author Phillip Webb
 */
public abstract class SpringFacesContext {

	private static ThreadLocal<SpringFacesContext> instance = new NamedThreadLocal<SpringFacesContext>(
			"Spring Faces Context");

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
	 * @return a {@link FacesContext} instance
	 */
	public abstract FacesContext getFacesContext();

	/**
	 * Returns the {@link WebApplicationContext} associated with this context.
	 * @return a {@link WebApplicationContext} instance
	 */
	public abstract WebApplicationContext getWebApplicationContext();

	/**
	 * Returns the MVC Handler associated with this context.
	 * @return the MVC handler
	 */
	public abstract Object getHandler();

	/**
	 * Returns the MVC controller associated with this context. If the {@link #getHandler() handler} is a Spring 3.1
	 * {@link HandlerMethod} this method will return the underling {@link HandlerMethod#getBean()}, in all other cases
	 * this method is identical to {@link #getHandler()}.
	 * @return the MVC controller
	 */
	public abstract Object getController();

	/**
	 * Render the specified {@link ModelAndViewArtifact} using JSF.
	 * @param modelAndViewArtifact the artifact to render
	 * @see #getRendering()
	 */
	public abstract void render(ModelAndViewArtifact modelAndViewArtifact);

	/**
	 * Return the {@link ModelAndViewArtifact} that is currently being {@link #render rendered} or <tt>null</tt> if no
	 * Spring Faces MVC request is being rendered.
	 * @return the {@link ModelAndViewArtifact} being rendered or <tt>null</tt>
	 */
	public abstract ModelAndViewArtifact getRendering();

	/**
	 * Returns the current {@link SpringFacesContext} instance or <tt>null</tt> the context is unavailable.
	 * @return the {@link SpringFacesContext} or <tt>null</tt>
	 */
	public static SpringFacesContext getCurrentInstance() {
		return getCurrentInstance(false);
	}

	/**
	 * Returns the current {@link SpringFacesContext}.
	 * @param required <tt>true</tt> if an {@link IllegalStateException} should be thrown when the context cannot be
	 * obtained. <tt>false</tt> if <tt>null</tt> should be returned when the context cannot be obtained
	 * @return the {@link SpringFacesContext} (or <tt>null</tt> if <tt>required</tt> is <tt>false</tt>)
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
	 * @param context the context instance to set or <tt>null</tt> to remove the current instance
	 */
	protected static void setCurrentInstance(SpringFacesContext context) {
		if (context == null) {
			instance.remove();
		} else {
			instance.set(context);
		}
	}

}
