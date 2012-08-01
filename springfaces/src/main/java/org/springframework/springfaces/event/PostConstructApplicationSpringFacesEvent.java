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
package org.springframework.springfaces.event;

import javax.faces.application.Application;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEventListener;

/**
 * A {@link SpringFacesApplicationEvent} that indicates that a JSF {@link Application} has been created. This event is
 * roughly equivalent to the standard JSF {@link PostConstructApplicationEvent event} with the exception that this event
 * is guaranteed to occur after Spring initialization. This event will be re-published whenever the Spring application
 * context is refreshed.
 * <p>
 * A common usage pattern is to use this event to trigger additional {@link Application#subscribeToEvent subscription}
 * of JSF {@link SystemEventListener}s.
 * 
 * @author Phillip Webb
 */
public class PostConstructApplicationSpringFacesEvent extends SpringFacesApplicationEvent {

	private static final long serialVersionUID = -1329729109758547764L;

	/**
	 * Create a new PostConstructFacesApplicationEvent.
	 * @param source the {@link Application} that caused the event to be published (never <code>null</code>)
	 */
	public PostConstructApplicationSpringFacesEvent(Application source) {
		super(source);
	}

	@Override
	public Application getSource() {
		return (Application) super.getSource();
	}
}
