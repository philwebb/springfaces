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
