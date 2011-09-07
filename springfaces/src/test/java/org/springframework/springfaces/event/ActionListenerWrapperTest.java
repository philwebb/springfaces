package org.springframework.springfaces.event;

import static org.mockito.Mockito.verify;

import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link ActionListenerWrapper}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionListenerWrapperTest {
	@Mock
	private ActionListener wrapped;

	private ActionListenerWrapper wrapper = new MockActionListenerWrapper();

	@Mock
	private ActionEvent event;

	@Test
	public void shouldWrapProcessAction() {
		wrapper.processAction(event);
		verify(wrapped).processAction(event);
	}

	private class MockActionListenerWrapper extends ActionListenerWrapper {
		@Override
		public ActionListener getWrapped() {
			return wrapped;
		}
	}
}
