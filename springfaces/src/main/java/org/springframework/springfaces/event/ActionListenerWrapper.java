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

import javax.faces.FacesWrapper;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * Provides a simple implementation of {@link ActionListener} that can be subclassed by developers wishing to provide
 * specialised behaviour to an existing {@link ActionListener instance} . The default implementation of all methods is
 * to call through to the wrapped {@link ActionListener}.
 * 
 * @author Phillip Webb
 */
public abstract class ActionListenerWrapper implements ActionListener, FacesWrapper<ActionListener> {

	public abstract ActionListener getWrapped();

	public void processAction(ActionEvent event) throws AbortProcessingException {
		getWrapped().processAction(event);
	}
}
