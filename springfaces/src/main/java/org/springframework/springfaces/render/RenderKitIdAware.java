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
package org.springframework.springfaces.render;

import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;

/**
 * Interface to be implemented by {@link ResponseStateManager}s that wish to be aware of the JSF <tt>renderKitId</tt>
 * being used. NOTE: Only {@link ResponseStateManager}s created from a {@link FacesWrapperFactory} will receive this
 * callback.
 * 
 * @author Phillip Webb
 */
public interface RenderKitIdAware {

	/**
	 * Callback that supplies the <tt>renderKitId</tt>.
	 * @param renderKitId the render kit ID
	 */
	void setRenderKitId(String renderKitId);
}
