package org.springframework.springfaces.internal;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ApplicationWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link ApplicationFactory} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringApplicationFactory extends ApplicationFactory {

	private final Log logger = LogFactory.getLog(getClass());
	private ApplicationFactory delegate;

	public SpringApplicationFactory(ApplicationFactory delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping ApplicationFactory " + delegate.getClass() + " to provide integration with Spring");
		}
		this.delegate = delegate;
	}

	@Override
	public Application getApplication() {
		Application application = delegate.getApplication();
		SpringApplication springApplication = SpringApplication.getSpringApplication(application);
		if (springApplication == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Wrapping Application " + application.getClass() + " to provide integration with Spring");
			}
			application = WrapperHandler.get(Application.class, application).getWrapped();
			application = new SpringApplication(application);
			setApplication(application);
		}
		return application;
	}

	@Override
	public void setApplication(Application application) {
		delegate.setApplication(application);
	}

	public static class SpringApplication extends ApplicationWrapper {

		private Application delegate;

		public SpringApplication(Application delegate) {
			this.delegate = delegate;
		}

		@Override
		public Application getWrapped() {
			System.out.println("getwrapped");
			new Exception().printStackTrace();
			return delegate;
		}

		@Override
		public void publishEvent(FacesContext context, Class<? extends SystemEvent> systemEventClass,
				Class<?> sourceBaseType, Object source) {
			System.out.println(systemEventClass);
			super.publishEvent(context, systemEventClass, sourceBaseType, source);
		}

		@Override
		public void publishEvent(FacesContext context, Class<? extends SystemEvent> systemEventClass, Object source) {
			System.out.println(systemEventClass);
			super.publishEvent(context, systemEventClass, source);
		}

		public static SpringApplication getSpringApplication(Application application) {
			FindSpringApplicationSystemEvent event = new FindSpringApplicationSystemEvent(application);
			return event.getSpringApplication();
		}
	}

	public static class FindSpringApplicationSystemEvent extends SystemEvent {

		private static final long serialVersionUID = 1L;
		private SpringApplication springApplication;

		public FindSpringApplicationSystemEvent(Object source) {
			super(source);
		}

		public void setSpringApplication(SpringApplication springApplication) {
			this.springApplication = springApplication;
		}

		public SpringApplication getSpringApplication() {
			return springApplication;
		}
	}
}
