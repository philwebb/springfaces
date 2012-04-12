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

import java.util.Map;

import org.springframework.util.Assert;

public final class ModelAndViewArtifact {

	private ViewArtifact viewArtifact;
	private Map<String, Object> model;

	public ModelAndViewArtifact(ViewArtifact viewArtifact, Map<String, Object> model) {
		super();
		Assert.notNull(viewArtifact, "ViewArtifact must not be null");
		this.viewArtifact = viewArtifact;
		this.model = model;
	}

	public ModelAndViewArtifact(String artifact, Map<String, Object> model) {
		this(new ViewArtifact(artifact), model);
	}

	public ModelAndViewArtifact(String artifact) {
		this(new ViewArtifact(artifact), null);
	}

	public ViewArtifact getViewArtifact() {
		return this.viewArtifact;
	}

	public Map<String, Object> getModel() {
		return this.model;
	}
}
