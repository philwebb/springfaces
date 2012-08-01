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
package org.springframework.springfaces.mvc.servlet.view;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.web.servlet.View;

/**
 * A Spring {@link View} that can be rendered via JSF.
 * 
 * @author Phillip Webb
 */
public interface FacesRenderedView extends View {

	/**
	 * Render the view given the specified model.
	 * @param model Map with name Strings as keys and corresponding model objects as values (Map can also be
	 * <code>null</code> in case of empty model)
	 * @param facesContext the faces context
	 * @throws Exception if rendering failed
	 */
	void render(Map<String, ?> model, FacesContext facesContext) throws Exception;

}
