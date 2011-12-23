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
		this.el.getValue(this.context, this.base, this.property);
		this.el.verify().getValue(this.context, this.base, this.property);
	}

	@Test
	public void shouldDelegateGetType() throws Exception {
		this.el.getType(this.context, this.base, this.property);
		this.el.verify().getType(this.context, this.base, this.property);
	}

	@Test
	public void shouldDelegateSetValue() throws Exception {
		Object value = new Object();
		this.el.setValue(this.context, this.base, this.property, value);
		this.el.verify().setValue(this.context, this.base, this.property, value);
	}

	@Test
	public void shouldDelegateIsReadOnly() throws Exception {
		this.el.isReadOnly(this.context, this.base, this.property);
		this.el.verify().isReadOnly(this.context, this.base, this.property);
	}

	@Test
	public void shouldDelegateGetFeatureDescriptors() throws Exception {
		this.el.getFeatureDescriptors(this.context, this.base);
		this.el.verify().getFeatureDescriptors(this.context, this.base);
	}

	@Test
	public void shouldDelegateGetCommonPropertyType() throws Exception {
		this.el.getCommonPropertyType(this.context, this.base);
		this.el.verify().getCommonPropertyType(this.context, this.base);
	}

	/**
	 * Version of {@link SpringELResolver} that uses a mock delegate that can be verified.
	 */
	private static class VerifiableSpringELResolver extends SpringELResolver {

		private CompositeELResolver delegate = mock(CompositeELResolver.class);

		@Override
		protected CompositeELResolver getDelegate() {
			return this.delegate;
		}

		public CompositeELResolver verify() {
			return Mockito.verify(this.delegate);
		}
	}
}
