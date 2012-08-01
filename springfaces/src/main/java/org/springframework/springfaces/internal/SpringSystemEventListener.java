/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.internal;

import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.SpringFacesIntegration;

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
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Processing system event " + event);
		}
		if (event instanceof PostConstructApplicationEvent) {
			processPostConstructApplicationEvent((PostConstructApplicationEvent) event);
		}
	}

	private void processPostConstructApplicationEvent(PostConstructApplicationEvent event) {
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		SpringApplication application = new SpringApplication(event.getApplication());
		factory.setApplication(application);
		SpringFacesIntegration.postConstructApplicationEvent(FacesContext.getCurrentInstance().getExternalContext(),
				application);
	}
}
