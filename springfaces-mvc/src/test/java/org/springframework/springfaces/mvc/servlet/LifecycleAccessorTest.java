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
package org.springframework.springfaces.mvc.servlet;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Iterator;

import javax.faces.FactoryFinder;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link LifecycleAccessor}.
 * 
 * @author Phillip Webb
 */
public class LifecycleAccessorTest {

	private static ThreadLocal<LifecycleAccessorTest> test = new ThreadLocal<LifecycleAccessorTest>();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private LifecycleAccessor lifecycleAccessor = new LifecycleAccessor();

	@Mock
	private ServletContext servletContext;

	@Mock
	private LifecycleFactory lifecycleFactory;

	@Mock
	private Lifecycle lifecycle;

	@Before
	public void setup() {
		test.set(this);
		MockitoAnnotations.initMocks(this);
		FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, MockLifecycleFactory.class.getName());
	}

	@Test
	public void shouldNeedServletContext() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("ServletContext has not been set");
		this.lifecycleAccessor.getLifecycle();
	}

	@Test
	public void shouldGetFromSpecifiedLifecycleId() throws Exception {
		given(this.lifecycleFactory.getLifecycle("example")).willReturn(this.lifecycle);
		this.lifecycleAccessor.setServletContext(this.servletContext);
		this.lifecycleAccessor.setLifecycleId("example");
		Lifecycle actual = this.lifecycleAccessor.getLifecycle();
		assertThat(actual, is(this.lifecycle));
		verify(this.lifecycleFactory).getLifecycle("example");
	}

	@Test
	public void shouldGetFromInitParam() throws Exception {
		given(this.servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR)).willReturn("example");
		given(this.lifecycleFactory.getLifecycle("example")).willReturn(this.lifecycle);
		this.lifecycleAccessor.setServletContext(this.servletContext);
		Lifecycle actual = this.lifecycleAccessor.getLifecycle();
		assertThat(actual, is(this.lifecycle));
		verify(this.lifecycleFactory).getLifecycle("example");
	}

	@Test
	public void shouldGetDefaultLifecycle() throws Exception {
		given(this.lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE)).willReturn(this.lifecycle);
		this.lifecycleAccessor.setServletContext(this.servletContext);
		Lifecycle actual = this.lifecycleAccessor.getLifecycle();
		assertThat(actual, is(this.lifecycle));
		verify(this.lifecycleFactory).getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

	}

	@Test
	public void shouldCacheLifecycle() throws Exception {
		given(this.lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE)).willReturn(this.lifecycle);
		this.lifecycleAccessor.setServletContext(this.servletContext);
		this.lifecycleAccessor.getLifecycle();
		this.lifecycleAccessor.getLifecycle();
		verify(this.lifecycleFactory, times(1)).getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		verifyNoMoreInteractions(this.lifecycleFactory);
	}

	@Test
	public void shouldResetOnSetLifecycleId() throws Exception {
		given(this.lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE)).willReturn(this.lifecycle);
		given(this.lifecycleFactory.getLifecycle("example")).willReturn(this.lifecycle);
		this.lifecycleAccessor.setServletContext(this.servletContext);
		this.lifecycleAccessor.getLifecycle();
		this.lifecycleAccessor.setLifecycleId("example");
		this.lifecycleAccessor.getLifecycle();
		verify(this.lifecycleFactory).getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		verify(this.lifecycleFactory).getLifecycle("example");

	}

	@Test
	public void shouldResetOnSetServletContext() throws Exception {
		given(this.lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE)).willReturn(this.lifecycle);
		this.lifecycleAccessor.setServletContext(this.servletContext);
		this.lifecycleAccessor.getLifecycle();
		this.lifecycleAccessor.setServletContext(this.servletContext);
		this.lifecycleAccessor.getLifecycle();
		verify(this.lifecycleFactory, times(2)).getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}

	public static class MockLifecycleFactory extends LifecycleFactory {

		@Override
		public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
			getMock().addLifecycle(lifecycleId, lifecycle);
		}

		@Override
		public Lifecycle getLifecycle(String lifecycleId) {
			return getMock().getLifecycle(lifecycleId);
		}

		@Override
		public Iterator<String> getLifecycleIds() {
			return getMock().getLifecycleIds();
		}

		private LifecycleFactory getMock() {
			return test.get().lifecycleFactory;
		}

	}
}
