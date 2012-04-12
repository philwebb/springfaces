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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.faces.FacesWrapper;

import org.junit.Test;
import org.springframework.core.GenericTypeResolver;
import org.springframework.springfaces.FacesWrapperFactory;

/**
 * Abstract base of {@link FacesWrapper} tests.
 * 
 * @param <T> Type type being wrapped
 * @param <W> The wrapper class
 * 
 * @author Phillip Webb
 */
public abstract class AbstractFacesWrapperTest<T, W extends FacesWrapper<T>> extends AbstractFacesWrapperFactoryTest {

	@SuppressWarnings("unchecked")
	protected Class<? extends T> getTypeClass() {
		return GenericTypeResolver.resolveTypeArguments(getClass(), AbstractFacesWrapperTest.class)[0];
	}

	@SuppressWarnings("unchecked")
	protected W newWrapper(T delegate) throws Exception {
		Class<?> type = GenericTypeResolver.resolveTypeArguments(getClass(), AbstractFacesWrapperTest.class)[1];
		return (W) type.getConstructor(getTypeClass()).newInstance(delegate);
	}

	@Test
	public void shouldWrap() throws Exception {
		@SuppressWarnings("unchecked")
		FacesWrapperFactory<T> wrapperFactory = mock(FacesWrapperFactory.class);
		addFactoryWrapper("wrapper", wrapperFactory);
		T delegate = mock(getTypeClass());
		W wrapper = newWrapper(delegate);
		T wrapped = mock(getTypeClass());
		given(wrapperFactory.newWrapper(getTypeClass(), delegate)).willReturn(wrapped);
		T actual = wrapper.getWrapped();
		assertSame(wrapped, actual);
	}
}
