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
package org.springframework.springfaces.mvc.render;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Strategy interface use to read and write {@link ViewArtifact} data.
 * @author Phillip Webb
 */
public interface FacesViewStateHandler {

	/**
	 * Write the specified view artifact.
	 * @param facesContext the faces context
	 * @param viewArtifact the view state to write
	 * @throws IOException on write error
	 */
	void write(FacesContext facesContext, ViewArtifact viewArtifact) throws IOException;

	/**
	 * Read previously saved view artifact. This method will be called during postback in order to restore state. NOTE:
	 * this method will be called for every JSF postback, implementations should take care to only restore artifacts
	 * that were previously {@link #write written}.
	 * @param request the request used to retrieve view state
	 * @return a ViewArtifact or <tt>null</tt> if the postback is not relevant
	 * @throws IOException on read error
	 */
	ViewArtifact read(HttpServletRequest request) throws IOException;
}
