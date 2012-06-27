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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link SpringExceptionHandlerFactory}.
 * @author Phillip Webb
 */
public class SpringExceptionHandlerFactoryTest {

	@Mock
	private ExceptionHandlerFactory delegate;

	private SpringExceptionHandlerFactory factory;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.factory = new SpringExceptionHandlerFactory(this.delegate);
	}

	@Test
	public void shouldWrapExceptionHandler() throws Exception {
		ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
		given(this.delegate.getExceptionHandler()).willReturn(exceptionHandler);
		ExceptionHandler actual = this.factory.getExceptionHandler();
		assertThat(actual, is(SpringExceptionHandler.class));
		assertThat(((SpringExceptionHandler) actual).getWrapped(), is(sameInstance(exceptionHandler)));
		// Check that the delegate is called
		actual.handle();
		verify(exceptionHandler).handle();
	}

}
