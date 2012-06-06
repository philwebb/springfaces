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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;

import org.junit.Test;

/**
 * Tests for {@link SpringRenderKit}.
 * 
 * @author Phillip Webb
 */
public class SpringRenderKitTest extends AbstractFacesWrapperTest<RenderKit, SpringRenderKit> {

	// Tests are inherited from AbstractWrapperTest

	@Override
	protected SpringRenderKit newWrapper(RenderKit delegate) throws Exception {
		return new SpringRenderKit("rid", delegate);
	}

	@Test
	public void shouldCreateWrappedResponseStateManager() throws Exception {
		RenderKit delegate = mock(RenderKit.class);
		ResponseStateManager responseStateManager = mock(ResponseStateManager.class);
		given(delegate.getResponseStateManager()).willReturn(responseStateManager);
		SpringRenderKit springRenderKit = new SpringRenderKit("rid", delegate);
		ResponseStateManager actual = springRenderKit.getResponseStateManager();
		assertThat(actual, is(instanceOf(SpringResponseStateManager.class)));
		verify(delegate).getResponseStateManager();
	}
}
