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
		this.wrapper.getNavigationCase(this.context, this.fromAction, this.outcome);
		verify(this.wrapped).getNavigationCase(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldWrapGetNavigationCases() throws Exception {
		this.wrapper.getNavigationCases();
		verify(this.wrapped).getNavigationCases();
	}

	@Test
	public void shouldWrapPerformNavigation() throws Exception {
		this.wrapper.performNavigation(this.outcome);
		verify(this.wrapped).performNavigation(this.outcome);
	}

	@Test
	public void shouldWrapHandleNavigation() throws Exception {
		this.wrapper.handleNavigation(this.context, this.fromAction, this.outcome);
		verify(this.wrapped).handleNavigation(this.context, this.fromAction, this.outcome);
	}

	private class MockConfigurableNavigationHandlerWrapper extends ConfigurableNavigationHandlerWrapper {
		@Override
		public ConfigurableNavigationHandler getWrapped() {
			return ConfigurableNavigationHandlerWrapperTest.this.wrapped;
		}
	}
}
