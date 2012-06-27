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

import static org.mockito.Mockito.verify;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link RenderKitFactoryWrapper}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class RenderKitFactoryWrapperTest {

	@Mock
	private RenderKitFactory wrapped;

	private RenderKitFactoryWrapper wrapper = new MockRenderKitFactoryWrapper();

	private String renderKitId = "renderKitId";

	@Mock
	private FacesContext context;

	@Mock
	private RenderKit renderKit;

	@Test
	public void shouldWrapAddRenderKit() {
		this.wrapper.addRenderKit(this.renderKitId, this.renderKit);
		verify(this.wrapped).addRenderKit(this.renderKitId, this.renderKit);
	}

	@Test
	public void shouldWrapGetRenderKit() {
		this.wrapper.getRenderKit(this.context, this.renderKitId);
		verify(this.wrapped).getRenderKit(this.context, this.renderKitId);
	}

	@Test
	public void shouldWrapGetRenderKitIds() {
		this.wrapper.getRenderKitIds();
		verify(this.wrapped).getRenderKitIds();
	}

	private class MockRenderKitFactoryWrapper extends RenderKitFactoryWrapper {
		@Override
		public RenderKitFactory getWrapped() {
			return RenderKitFactoryWrapperTest.this.wrapped;
		}
	}
}
