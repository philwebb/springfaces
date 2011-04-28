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
