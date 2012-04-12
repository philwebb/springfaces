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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.faces.FacesWrapper;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.event.PostConstructApplicationEvent;

import org.junit.Test;

/**
 * Tests for {@link SpringSystemEventListener}.
 * 
 * @author Phillip Webb
 */
public class SpringSystemEventListenerTest extends AbstractFacesWrapperFactoryTest {

	private SpringSystemEventListener listener = new SpringSystemEventListener();

	@Test
	public void shouldListenForAllSource() throws Exception {
		assertTrue(this.listener.isListenerForSource(new Object()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSetSpringApplication() throws Exception {
		PostConstructApplicationEvent event = mock(PostConstructApplicationEvent.class);
		Application application = mock(Application.class);
		given(event.getApplication()).willReturn(application);
		FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
		try {
			this.listener.processEvent(event);
			MockApplicationFactory applicationFactory = (MockApplicationFactory) FactoryFinder
					.getFactory(FactoryFinder.APPLICATION_FACTORY);
			Application actual = applicationFactory.getApplication();
			assertTrue(actual instanceof SpringApplication);
			assertSame(application, ((FacesWrapper<Application>) actual).getWrapped());
		} finally {
			FactoryFinder.releaseFactories();
			FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, null);
		}
	}

	public static class MockApplicationFactory extends ApplicationFactory {

		private Application application = mock(Application.class);

		@Override
		public Application getApplication() {
			return this.application;
		}

		@Override
		public void setApplication(Application application) {
			this.application = application;
		}
	}
}
