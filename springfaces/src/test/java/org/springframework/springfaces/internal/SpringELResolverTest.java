package org.springframework.springfaces.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.el.CompositeELResolver;
import javax.el.ELContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.springfaces.FacesWrapperFactory;

/**
 * Tests for {@link SpringELResolver}.
 * 
 * @author Phillip Webb
 */
public class SpringELResolverTest extends AbstractFacesWrapperFactoryTest {

	private VerifiableSpringELResolver el = new VerifiableSpringELResolver();
	private ELContext context = mock(ELContext.class);
	private Object base = new Object();
	private Object property = new Object();

	@Test
	public void shouldWrap() throws Exception {
		@SuppressWarnings("unchecked")
		FacesWrapperFactory<CompositeELResolver> wrapperFactory = mock(FacesWrapperFactory.class);
		addFactoryWrapper("wrapper", wrapperFactory);
		SpringELResolver resolver = new SpringELResolver();
		resolver.getDelegate();
		verify(wrapperFactory).newWrapper(eq(CompositeELResolver.class), any(CompositeELResolver.class));
	}

	@Test
	public void shouldDelegateGetValue() throws Exception {
		el.getValue(context, base, property);
		el.verify().getValue(context, base, property);
	}

	@Test
	public void shouldDelegateGetType() throws Exception {
		el.getType(context, base, property);
		el.verify().getType(context, base, property);
	}

	@Test
	public void shouldDelegateSetValue() throws Exception {
		Object value = new Object();
		el.setValue(context, base, property, value);
		el.verify().setValue(context, base, property, value);
	}

	@Test
	public void shouldDelegateIsReadOnly() throws Exception {
		el.isReadOnly(context, base, property);
		el.verify().isReadOnly(context, base, property);
	}

	@Test
	public void shouldDelegateGetFeatureDescriptors() throws Exception {
		el.getFeatureDescriptors(context, base);
		el.verify().getFeatureDescriptors(context, base);
	}

	@Test
	public void shouldDelegateGetCommonPropertyType() throws Exception {
		el.getCommonPropertyType(context, base);
		el.verify().getCommonPropertyType(context, base);
	}

	/**
	 * Version of {@link SpringELResolver} that uses a mock delegate that can be verified.
	 */
	private static class VerifiableSpringELResolver extends SpringELResolver {

		private CompositeELResolver delegate = mock(CompositeELResolver.class);

		@Override
		protected CompositeELResolver getDelegate() {
			return delegate;
		}

		public CompositeELResolver verify() {
			return Mockito.verify(delegate);
		}
	}
}
