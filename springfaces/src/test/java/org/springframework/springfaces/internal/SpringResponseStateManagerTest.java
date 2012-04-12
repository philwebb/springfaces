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
package org.springframework.springfaces.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import javax.faces.render.ResponseStateManager;

import org.junit.Test;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.render.RenderKitIdAware;

/**
 * Tests for {@link SpringResponseStateManager}.
 * 
 * @author Phillip Webb
 */
public class SpringResponseStateManagerTest extends
		AbstractFacesWrapperTest<ResponseStateManager, SpringResponseStateManager> {

	@Override
	protected SpringResponseStateManager newWrapper(ResponseStateManager delegate) throws Exception {
		return new SpringResponseStateManager("rid", delegate);
	}

	@Test
	public void shouldSetRenderKitId() throws Exception {
		@SuppressWarnings("unchecked")
		FacesWrapperFactory<ResponseStateManager> wrapperFactory = mock(FacesWrapperFactory.class);
		addFactoryWrapper("wrapper", wrapperFactory);
		ResponseStateManager delegate = mock(getTypeClass());
		SpringResponseStateManager wrapper = newWrapper(delegate);
		ResponseStateManager wrapped = mock(getTypeClass(), withSettings().extraInterfaces(RenderKitIdAware.class));
		given(wrapperFactory.newWrapper(getTypeClass(), delegate)).willReturn(wrapped);
		wrapper.getWrapped();
		verify((RenderKitIdAware) wrapped).setRenderKitId("rid");
	}
}
