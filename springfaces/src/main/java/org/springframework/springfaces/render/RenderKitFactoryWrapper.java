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

import java.util.Iterator;

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * Provides a simple implementation of {@link RenderKitFactory} that can be subclassed by developers wishing to provide
 * Specialized behavior to an existing {@link RenderKitFactory instance}. The default implementation of all methods is
 * to call through to the wrapped {@link RenderKitFactory}.
 * @author Phillip Webb
 */
public abstract class RenderKitFactoryWrapper extends RenderKitFactory implements FacesWrapper<RenderKitFactory> {

	@Override
	public abstract RenderKitFactory getWrapped();

	@Override
	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		getWrapped().addRenderKit(renderKitId, renderKit);
	}

	@Override
	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
		return getWrapped().getRenderKit(context, renderKitId);
	}

	@Override
	public Iterator<String> getRenderKitIds() {
		return getWrapped().getRenderKitIds();
	}
}
