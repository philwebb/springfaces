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

import org.springframework.context.ApplicationEvent;

/**
 * Abstract base of all {@link ApplicationEvent}s that originate from JSF.
 * 
 * @author Phillip Webb
 */
public abstract class SpringFacesApplicationEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4769349942324437586L;

	/**
	 * Create a new FacesApplicationEvent.
	 * @param source the component that published the event (never <code>null</code>)
	 */
	public SpringFacesApplicationEvent(Object source) {
		super(source);
	}
}
