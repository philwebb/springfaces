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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link DispatcherAwareBeanPostProcessor}.
 * @author Phillip Webb
 */
public class DispatcherAwareBeanPostProcessorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldNeedDispatcher() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Dispatcher must not be null");
		new DispatcherAwareBeanPostProcessor(null);
	}

	@Test
	public void shouldInject() throws Exception {
		Dispatcher dispatcher = mock(Dispatcher.class);
		DispatcherAware bean = mock(DispatcherAware.class);
		DispatcherAwareBeanPostProcessor processor = new DispatcherAwareBeanPostProcessor(dispatcher);
		assertThat(processor.postProcessBeforeInitialization(bean, "beanName"), is((Object) bean));
		assertThat(processor.postProcessAfterInitialization(bean, "beanName"), is((Object) bean));
		verify(bean).setDispatcher(dispatcher);
	}
}
