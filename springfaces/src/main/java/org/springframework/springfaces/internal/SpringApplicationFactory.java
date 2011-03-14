package org.springframework.springfaces.internal;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ApplicationWrapper;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

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

		private WrapperHandler<Application> wrapperHandler;

		public SpringApplication(Application delegate) {
			wrapperHandler = WrapperHandler.get(Application.class, delegate);
		}

		@Override
		public Application getWrapped() {
			return wrapperHandler.getWrapped();
		}

		@Override
		public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass, Class<?> sourceClass,
				SystemEventListener listener) {
			if (FindSpringApplicationSystemEventListener.class.equals(systemEventClass)) {
				((FindSpringApplicationSystemEventListener) listener).setSpringApplication(this);
			}
			super.subscribeToEvent(systemEventClass, sourceClass, listener);
		}

		public static SpringApplication getSpringApplication(Application application) {
			if (application instanceof SpringApplication) {
				return (SpringApplication) application;
			}
			FindSpringApplicationSystemEventListener listener = new FindSpringApplicationSystemEventListener();
			application.subscribeToEvent(FindSpringApplicationSystemEvent.class, listener);
			application.unsubscribeFromEvent(FindSpringApplicationSystemEvent.class, listener);
			return listener.getSpringApplication();
		}
	}

	public static class FindSpringApplicationSystemEvent extends SystemEvent {
		private static final long serialVersionUID = 1L;

		public FindSpringApplicationSystemEvent(Object source) {
			super(source);
		}
	}

	public static class FindSpringApplicationSystemEventListener implements SystemEventListener {

		private SpringApplication springApplication;

		public void setSpringApplication(SpringApplication springApplication) {
			this.springApplication = springApplication;
		}

		public SpringApplication getSpringApplication() {
			return springApplication;
		}

		public void processEvent(SystemEvent event) throws AbortProcessingException {
		}

		public boolean isListenerForSource(Object source) {
			return false;
		}
	}
}
