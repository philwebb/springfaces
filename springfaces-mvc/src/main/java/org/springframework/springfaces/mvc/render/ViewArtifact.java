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

import java.io.Serializable;

import javax.faces.view.ViewDeclarationLanguage;

/**
 * A reference to an artifact that contains the {@link ViewDeclarationLanguage VDL} syntax of a JSF view. In most cases
 * a <tt>ViewArtifact</tt> will refer to the location of a facelet file, for example: <tt>/WEB-INF/pages/page.xhtml</tt>
 * .
 * 
 * @author Phillip Webb
 */
public final class ViewArtifact implements Serializable {

	private static final long serialVersionUID = 1L;

	private String artifact;

	public ViewArtifact(String artifact) {
		super();
		// FIXME ANN
		this.artifact = artifact;
	}

	@Override
	public String toString() {
		return this.artifact;
	}
}
