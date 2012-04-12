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
		return this.wrapperHandler.getWrapped();
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
