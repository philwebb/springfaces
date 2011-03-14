package org.springframework.springfaces.internal;

import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link SystemEventListener} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringSystemEventListener implements SystemEventListener {

	private final Log logger = LogFactory.getLog(getClass());

	public boolean isListenerForSource(Object source) {
		return true;
	}

	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (logger.isDebugEnabled()) {
			logger.debug("Processing system event " + event);
		}
		if (event instanceof PostConstructApplicationEvent) {
			processPostConstructApplicationEvent((PostConstructApplicationEvent) event);
		}
	}

	private void processPostConstructApplicationEvent(PostConstructApplicationEvent event) {
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		factory.setApplication(new SpringApplication(event.getApplication()));
	}

}
