package org.springframework.springfaces.internal;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.springfaces.internal.WrapperHandler.DelegateAccessType;

/**
 * An {@link ELResolver} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
@SuppressWarnings("rawtypes")
public class SpringELResolver extends ELResolver {

	private WrapperHandler<CompositeELResolver> wrapperHandler = new WrapperHandler<CompositeELResolver>(
			CompositeELResolver.class, new CompositeELResolverFactory());

	protected CompositeELResolver getDelegate() {
		return wrapperHandler.getWrapped();
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		return getDelegate().getValue(context, base, property);
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		return getDelegate().getType(context, base, property);
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		getDelegate().setValue(context, base, property, value);
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return getDelegate().isReadOnly(context, base, property);
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return getDelegate().getFeatureDescriptors(context, base);
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return getDelegate().getCommonPropertyType(context, base);
	}

	/**
	 * {@link WrapperHandler.DelegateAccessor} that creates a new {@link CompositeELResolver} each time.
	 */
	private static class CompositeELResolverFactory implements WrapperHandler.DelegateAccessor<CompositeELResolver> {

		public String getDescription() {
			return CompositeELResolver.class.getName();
		}

		public CompositeELResolver getDelegate(DelegateAccessType accessType) {
			return new CompositeELResolver();
		}
	}
}
