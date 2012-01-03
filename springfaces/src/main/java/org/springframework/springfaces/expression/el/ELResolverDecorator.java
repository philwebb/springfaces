package org.springframework.springfaces.expression.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.util.Assert;

/**
 * Base for an {@link ELResolver} <tt>Decorator</tt>.
 * 
 * @author Phillip Webb
 */
public class ELResolverDecorator extends ELResolver {

	private ELResolver resolver;

	public ELResolverDecorator(ELResolver resolver) {
		Assert.notNull(resolver, "Resolver must not be null");
		this.resolver = resolver;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		return this.resolver.getValue(context, base, property);
	}

	@Override
	public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
		return this.resolver.invoke(context, base, method, paramTypes, params);
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		return this.resolver.getType(context, base, property);
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		this.resolver.setValue(context, base, property, value);
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return this.resolver.isReadOnly(context, base, property);
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return this.resolver.getFeatureDescriptors(context, base);
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return this.resolver.getCommonPropertyType(context, base);
	}

}
