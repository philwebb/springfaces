package org.springframework.springfaces;

import java.util.Date;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.event.PostConstructApplicationSpringFacesEvent;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * Bean that can be registered within a {@link WebApplicationContext} to enable integration between Spring and Java
 * Server Faces.
 * 
 * @see FacesWrapperFactory
 * 
 * @author Phillip Webb
 */
public class SpringFacesIntegration extends WebApplicationObjectSupport implements
		ApplicationListener<ContextRefreshedEvent> {

	/**
	 * The {@link ServletContext} attribute that will contain the {@link SpringFacesIntegration} instance.
	 */
	private static final String ATTRIBUTE = SpringFacesIntegration.class.getName();

	/**
	 * The {@link ServletContext} attribute that will contain the date the {@link WebApplicationContext} containing
	 * {@link SpringFacesIntegration} was last refreshed.
	 */
	private static final String LAST_REFRESHED_DATE_ATTRIBUTE = SpringFacesIntegration.class.getName() + ".DATE";

	/**
	 * The {@link ServletContext} attribute that will contain the active JSF {@link Application} (if any).
	 */
	private static final String APPLICATION_ATTRIBUTE = SpringFacesIntegration.class.getName() + ".APPLICATION";

	@Override
	protected void initApplicationContext() throws BeansException {
		getServletContext().setAttribute(ATTRIBUTE, this);
		getServletContext().setAttribute(LAST_REFRESHED_DATE_ATTRIBUTE, new Date());
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		getServletContext().setAttribute(LAST_REFRESHED_DATE_ATTRIBUTE, new Date());
		Application application = (Application) getServletContext().getAttribute(APPLICATION_ATTRIBUTE);
		if (application != null) {
			publishPostConstructApplicationEvent(application);
		}
	}

	protected final void publishPostConstructApplicationEvent(Application application) {
		getApplicationContext().publishEvent(new PostConstructApplicationSpringFacesEvent(application));
	}

	/**
	 * Determine if {@link SpringFacesIntegration} has been installed. This method will return <tt>true</tt> when a
	 * {@link WebApplicationContext} containing a {@link SpringFacesIntegration} bean has been fully loaded.
	 * @param servletContext the servlet context
	 * @return <tt>true</tt> if {@link SpringFacesIntegration} is installed and has loaded
	 * @see #isInstalled(ExternalContext)
	 */
	public static boolean isInstalled(ServletContext servletContext) {
		Assert.notNull(servletContext, "ServletContext must not be null");
		return isInstalled(servletContext.getAttribute(ATTRIBUTE));
	}

	/**
	 * Determine if {@link SpringFacesIntegration} has been installed. This method will return <tt>true</tt> when a
	 * {@link WebApplicationContext} containing a {@link SpringFacesIntegration} bean has been fully loaded.
	 * @param externalContext the JSF external context
	 * @return <tt>true</tt> if {@link SpringFacesIntegration} is installed and has loaded
	 * @see #isInstalled(ServletContext)
	 */
	public static boolean isInstalled(ExternalContext externalContext) {
		Assert.notNull(externalContext, "ExternalContext must not be null");
		return isInstalled(externalContext.getApplicationMap().get(ATTRIBUTE));
	}

	private static boolean isInstalled(Object springFacesIntegration) {
		return springFacesIntegration != null;
	}

	/**
	 * Determine the date that the {@link WebApplicationContext} containing the {@link SpringFacesIntegration} bean was
	 * last refreshed.
	 * @param servletContext the servlet context
	 * @return the {@link Date} of that the {@link WebApplicationContext} was last refreshed
	 * @throws IllegalStateException if {@link SpringFacesIntegration} is not {@link #isInstalled(ServletContext)
	 * installed}
	 * @see #isInstalled(ServletContext)
	 * @see #getLastRefreshedDate(ExternalContext)
	 */
	public static Date getLastRefreshedDate(ServletContext servletContext) {
		Assert.notNull(servletContext, "ServletContext must not be null");
		return getLastRefreshedDate(servletContext.getAttribute(LAST_REFRESHED_DATE_ATTRIBUTE));
	}

	/**
	 * Determine the date that the {@link WebApplicationContext} containing the {@link SpringFacesIntegration} bean was
	 * last refreshed.
	 * @param externalContext the JSF external context
	 * @return the {@link Date} of that the {@link WebApplicationContext} was last refreshed
	 * @throws IllegalStateException if {@link SpringFacesIntegration} is not {@link #isInstalled(ExternalContext)
	 * installed}
	 * @see #isInstalled(ExternalContext)
	 * @see #getLastRefreshedDate(ServletContext)
	 */
	public static Date getLastRefreshedDate(ExternalContext externalContext) {
		Assert.notNull(externalContext, "ExternalContext must not be null");
		return getLastRefreshedDate(externalContext.getApplicationMap().get(LAST_REFRESHED_DATE_ATTRIBUTE));
	}

	private static Date getLastRefreshedDate(Object lastRefreshDate) {
		Assert.state(lastRefreshDate != null, "Unable to determine the last refresh date for SpringFaces, has "
				+ SpringFacesIntegration.class.getSimpleName() + " been configured?");
		Assert.isInstanceOf(Date.class, lastRefreshDate);
		return (Date) lastRefreshDate;
	}

	/**
	 * Return the current {@link SpringFacesIntegration} instance registered for the give <tt>servletContext</tt>
	 * @param servletContext the servlet context
	 * @return the {@link SpringFacesIntegration} instance
	 * @throws IllegalStateException if {@link SpringFacesIntegration} is not {@link #isInstalled(ServletContext)
	 * installed}
	 * @see #isInstalled(ServletContext)
	 */
	public static SpringFacesIntegration getCurrentInstance(ServletContext servletContext) {
		Assert.notNull(servletContext, "ServletContext must not be null");
		return getCurrentInstance(servletContext.getAttribute(ATTRIBUTE));
	}

	/**
	 * Return the current {@link SpringFacesIntegration} instance registered for the give <tt>externalContext</tt>
	 * @param externalContext the JSF external context
	 * @return the {@link SpringFacesIntegration} instance
	 * @throws IllegalStateException if {@link SpringFacesIntegration} is not {@link #isInstalled(ExternalContext)
	 * installed}
	 * @see #isInstalled(ExternalContext)
	 */
	public static SpringFacesIntegration getCurrentInstance(ExternalContext externalContext) {
		Assert.notNull(externalContext, "ExternalContext must not be null");
		return getCurrentInstance(externalContext.getApplicationMap().get(ATTRIBUTE));
	}

	private static SpringFacesIntegration getCurrentInstance(Object instance) {
		Assert.state(instance != null, "Unable to obtain the " + SpringFacesIntegration.class.getSimpleName()
				+ " instance.");
		Assert.isInstanceOf(SpringFacesIntegration.class, instance);
		return (SpringFacesIntegration) instance;
	}

	/**
	 * Indicates that an JSF {@link Application} has been created. This this an internal method and not intended to be
	 * called by clients. Note: It is possible for the JSF {@link Application} to be created before Spring has
	 * initialized.
	 * @param externalContext the JSF external context
	 * @param application the newly created application
	 */
	public static void postConstructApplicationEvent(ExternalContext externalContext, Application application) {
		externalContext.getApplicationMap().put(APPLICATION_ATTRIBUTE, application);
		if (isInstalled(externalContext)) {
			// We have already loaded, we need to fire an event
			getCurrentInstance(externalContext).publishPostConstructApplicationEvent(application);
		}
	}
}
