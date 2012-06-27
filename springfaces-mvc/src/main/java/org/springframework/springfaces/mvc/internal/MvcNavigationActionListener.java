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
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * Action listener that stores the {@link ActionEvent} so that the {@link MvcNavigationHandler} can obtain it later.
 * @author Phillip Webb
 */
public class MvcNavigationActionListener implements ActionListener {

	private static final String KEY = MvcNavigationActionListener.class.getName();

	private ActionListener delegate;

	public MvcNavigationActionListener(ActionListener delegate) {
		this.delegate = delegate;
	}

	public void processAction(ActionEvent event) throws AbortProcessingException {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getAttributes().put(KEY, event);
		this.delegate.processAction(event);
	}

	/**
	 * Returns the last {@link ActionEvent} that occurred.
	 * @param context the faces context
	 * @return the action event or <tt>null</tt>
	 */
	public static ActionEvent getLastActionEvent(FacesContext context) {
		return (ActionEvent) context.getAttributes().get(KEY);
	}
}
