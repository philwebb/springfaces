package org.springframework.springfaces.internal;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
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
		if (event instanceof PostConstructApplicationEvent) {
			processPostConstructApplicationEvent((PostConstructApplicationEvent) event);
		}
	}

	private void processPostConstructApplicationEvent(PostConstructApplicationEvent event) {
		Application application = event.getApplication();
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping Application " + application.getClass() + " to provide integration with Spring");
		}
		Application wrapped = WrapperHandler.get(Application.class, application).getWrapped();
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		factory.setApplication(wrapped);
	}

}
