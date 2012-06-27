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
import org.springframework.util.ObjectUtils;

/**
 * Holder for both Model and {@link ViewArtifact}.
 * @author Phillip Webb
 */
public final class ModelAndViewArtifact {

	private ViewArtifact viewArtifact;

	private Map<String, Object> model;

	/**
	 * Create a new {@link ModelAndViewArtifact} instance.
	 * @param viewArtifact the view artifact
	 * @param model the model or <tt>null</tt>
	 */
	public ModelAndViewArtifact(ViewArtifact viewArtifact, Map<String, Object> model) {
		super();
		Assert.notNull(viewArtifact, "ViewArtifact must not be null");
		this.viewArtifact = viewArtifact;
		this.model = model;
	}

	/**
	 * Create a new {@link ModelAndViewArtifact} instance.
	 * @param artifact the view artifact
	 * @param model the model or <tt>null</tt>
	 */
	public ModelAndViewArtifact(String artifact, Map<String, Object> model) {
		this(new ViewArtifact(artifact), model);
	}

	/**
	 * Create a new {@link ModelAndViewArtifact} instance.
	 * @param artifact the view artifact
	 */
	public ModelAndViewArtifact(String artifact) {
		this(new ViewArtifact(artifact), null);
	}

	/**
	 * Returns the view artifact.
	 * @return the view artifact
	 */
	public ViewArtifact getViewArtifact() {
		return this.viewArtifact;
	}

	/**
	 * Returns the model or <tt>null</tt> if not model is available.
	 * @return the model or <tt>null</tt>
	 */
	public Map<String, Object> getModel() {
		return this.model;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + this.viewArtifact.hashCode();
		result = prime * result + ObjectUtils.nullSafeHashCode(this.model);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() == obj.getClass()) {
			ModelAndViewArtifact other = (ModelAndViewArtifact) obj;
			return this.viewArtifact.equals(other.viewArtifact) && ObjectUtils.nullSafeEquals(this.model, other.model);
		}
		return super.equals(obj);
	}

}
