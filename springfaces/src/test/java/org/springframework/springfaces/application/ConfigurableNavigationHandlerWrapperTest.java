package org.springframework.springfaces.application;

import static org.mockito.Mockito.verify;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link ConfigurableNavigationHandlerWrapper}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurableNavigationHandlerWrapperTest {

	@Mock
	private ConfigurableNavigationHandler wrapped;

	private ConfigurableNavigationHandlerWrapper wrapper = new MockConfigurableNavigationHandlerWrapper();

	@Mock
	private FacesContext context;

	private String fromAction = "fromAction";

	private String outcome = "outcome";

	@Test
	public void shouldWrapGetNavigationCase() throws Exception {
		wrapper.getNavigationCase(context, fromAction, outcome);
		verify(wrapped).getNavigationCase(context, fromAction, outcome);
	}

	@Test
	public void shouldWrapGetNavigationCases() throws Exception {
		wrapper.getNavigationCases();
		verify(wrapped).getNavigationCases();
	}

	@Test
	public void shouldWrapPerformNavigation() throws Exception {
		wrapper.performNavigation(outcome);
		verify(wrapped).performNavigation(outcome);
	}

	@Test
	public void shouldWrapHandleNavigation() throws Exception {
		wrapper.handleNavigation(context, fromAction, outcome);
		verify(wrapped).handleNavigation(context, fromAction, outcome);
	}

	private class MockConfigurableNavigationHandlerWrapper extends ConfigurableNavigationHandlerWrapper {
		@Override
		public ConfigurableNavigationHandler getWrapped() {
			return wrapped;
		}
	}
}
