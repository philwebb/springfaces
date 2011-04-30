package org.springframework.springfaces.util;

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
		wrapper.writeState(context, stateObject);
		verify(wrapped).writeState(context, stateObject);
	}

	@Test
	public void shouldWrapWriteStateSerializedView() throws Exception {
		wrapper.writeState(context, stateSerializedView);
		verify(wrapped).writeState(context, stateSerializedView);
	}

	@Test
	public void shouldWrapGetState() {
		wrapper.getState(context, viewId);
		verify(wrapped).getState(context, viewId);
	}

	@Test
	public void shouldWrapGetTreeStructureToRestore() {
		wrapper.getTreeStructureToRestore(context, viewId);
		verify(wrapped).getTreeStructureToRestore(context, viewId);
	}

	@Test
	public void shouldWrapGetComponentStateToRestore() {
		wrapper.getComponentStateToRestore(context);
		verify(wrapped).getComponentStateToRestore(context);
	}

	@Test
	public void shouldWrapIsPostback() {
		wrapper.isPostback(context);
		verify(wrapped).isPostback(context);
	}

	@Test
	public void shouldWrapGetViewState() {
		wrapper.getViewState(context, stateObject);
		verify(wrapped).getViewState(context, stateObject);
	}

	private class MockResponseStateManagerWrapper extends ResponseStateManagerWrapper {
		@Override
		public ResponseStateManager getWrapped() {
			return wrapped;
		}
	}
}
