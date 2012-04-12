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
