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
package org.springframework.springfaces.mvc.servlet;

import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.util.Assert;

/**
 * Used to pass JSF postback data from the {@link FacesHandlerInterceptor} to the {@link FacesPostbackHandler}.
 * 
 * @author Phillip Webb
 * @see FacesHandlerInterceptor
 * @see FacesPostbackHandler
 */
public class Postback {

	private ViewArtifact viewArtifact;
	private Object handler;

	public Postback(ViewArtifact viewArtifact, Object handler) {
		Assert.notNull(viewArtifact, "ViewArtifact must not be null");
		Assert.notNull(handler, "Handler must not be null");
		this.viewArtifact = viewArtifact;
		this.handler = handler;
	}

	public ViewArtifact getViewArtifact() {
		return this.viewArtifact;
	}

	public Object getHandler() {
		return this.handler;
	}
}
