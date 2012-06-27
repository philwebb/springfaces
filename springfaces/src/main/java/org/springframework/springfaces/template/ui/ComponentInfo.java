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
package org.springframework.springfaces.template.ui;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIMessages;

/**
 * Information about one or more {@link UIComponent components}.
 * @see UIComponentInfo
 * @author Phillip Webb
 */
public interface ComponentInfo {

	/**
	 * Returns the first component that the information was created from.
	 * @return the first component
	 * @see #getComponents()
	 */
	UIComponent getComponent();

	/**
	 * Returns all components that the information was created from.
	 * @return all components
	 */
	List<UIComponent> getComponents();

	/**
	 * Returns <tt>true</tt> if all {@link #getComponents() components} are valid.
	 * @return if all components are valid
	 */
	boolean isValid();

	/**
	 * Returns <tt>true</tt> if any of the {@link #getComponents() components} are required.
	 * @return if any component is required
	 */
	boolean isRequired();

	/**
	 * Returns the label of the first component. If no label is available an {@link IllegalStateException} is thrown.
	 * @return the label
	 */
	String getLabel();

	/**
	 * Return the ID of the component that should be used when setting the 'for' attribute of components such as
	 * {@link UIMessages}.
	 * @return the for ID
	 */
	String getFor();
}
