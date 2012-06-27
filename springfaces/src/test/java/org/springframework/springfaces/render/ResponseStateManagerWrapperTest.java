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

import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link ResponseStateManagerWrapper}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("deprecation")
public class ResponseStateManagerWrapperTest {

	@Mock
	private ResponseStateManager wrapped;

	private ResponseStateManagerWrapper wrapper = new MockResponseStateManagerWrapper();

	@Mock
	private FacesContext context;

	private Object stateObject = new Object();

	@Mock
	private SerializedView stateSerializedView;

	private String viewId = "viewId";

	@Test
	public void shouldWrapWriteStateObject() throws Exception {
		this.wrapper.writeState(this.context, this.stateObject);
		verify(this.wrapped).writeState(this.context, this.stateObject);
	}

	@Test
	public void shouldWrapWriteStateSerializedView() throws Exception {
		this.wrapper.writeState(this.context, this.stateSerializedView);
		verify(this.wrapped).writeState(this.context, this.stateSerializedView);
	}

	@Test
	public void shouldWrapGetState() {
		this.wrapper.getState(this.context, this.viewId);
		verify(this.wrapped).getState(this.context, this.viewId);
	}

	@Test
	public void shouldWrapGetTreeStructureToRestore() {
		this.wrapper.getTreeStructureToRestore(this.context, this.viewId);
		verify(this.wrapped).getTreeStructureToRestore(this.context, this.viewId);
	}

	@Test
	public void shouldWrapGetComponentStateToRestore() {
		this.wrapper.getComponentStateToRestore(this.context);
		verify(this.wrapped).getComponentStateToRestore(this.context);
	}

	@Test
	public void shouldWrapIsPostback() {
		this.wrapper.isPostback(this.context);
		verify(this.wrapped).isPostback(this.context);
	}

	@Test
	public void shouldWrapGetViewState() {
		this.wrapper.getViewState(this.context, this.stateObject);
		verify(this.wrapped).getViewState(this.context, this.stateObject);
	}

	private class MockResponseStateManagerWrapper extends ResponseStateManagerWrapper {
		@Override
		public ResponseStateManager getWrapped() {
			return ResponseStateManagerWrapperTest.this.wrapped;
		}
	}
}
