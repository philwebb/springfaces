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
 * 
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
