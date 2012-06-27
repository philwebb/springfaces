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
package org.springframework.springfaces.mvc.internal;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * System Event Listener that stores the {@link PreRenderComponentEvent} so that the {@link MvcNavigationHandler} can
 * obtain it later.
 * @author Phillip Webb
 */
public class MvcNavigationSystemEventListener implements SystemEventListener {

	private static final String KEY = MvcNavigationSystemEventListener.class.getName();

	public boolean isListenerForSource(Object source) {
		return true;
	}

	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PreRenderComponentEvent) {
			processEvent((PreRenderComponentEvent) event);
		}
	}

	private void processEvent(PreRenderComponentEvent event) throws AbortProcessingException {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getAttributes().put(KEY, event);
	}

	/**
	 * Returns the last {@link PreRenderComponentEvent} that occurred.
	 * @param context the faces context
	 * @return the action event or <tt>null</tt>
	 */
	public static PreRenderComponentEvent getLastPreRenderComponentEvent(FacesContext context) {
		return (PreRenderComponentEvent) context.getAttributes().get(KEY);
	}
}
