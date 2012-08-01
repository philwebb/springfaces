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

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * Spring read-only EL property accessor that acts as an adapter to a Java {@link ELContext}. Subclasses must provide
 * {@link #getElContext access} to an actual {@link ELContext}.
 * 
 * @author Phillip Webb
 * @see #getElContext
 * @see #getResolveBase
 * @see #getResolveProperty
 */
public abstract class ELPropertyAccessor implements PropertyAccessor {

	public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
		return (resolveValue(context, target, name) != null);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
		return resolveValue(context, target, name);
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
		return false;
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
	}

	public Class<?>[] getSpecificTargetClasses() {
		return new Class[] { BeanExpressionContext.class };
	}

	/**
	 * Resolve a value from the underlying {@link ELContext}.
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @return The resolved value or <tt>null</tt> if the property cannot be resolved
	 * @throws AccessException
	 */
	protected TypedValue resolveValue(EvaluationContext context, Object target, String name) throws AccessException {
		ELContext elContext = getElContext(context, target);
		if (elContext != null) {
			ELResolver resolver = elContext.getELResolver();
			Object base = getResolveBase(context, target, name);
			Object property = getResolveProperty(context, target, name);
			Class<?> type = resolver.getType(elContext, base, property);
			Object value = resolver.getValue(elContext, base, property);
			if (elContext.isPropertyResolved()) {
				return new TypedValue(value, TypeDescriptor.valueOf(type));
			}
		}
		return null;
	}

	/**
	 * Strategy method used to obtain the Java {@link ELContext} to use.
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @return The {@link ELContext} to use or <tt>null</tt> to skip this accessor
	 */
	protected abstract ELContext getElContext(EvaluationContext context, Object target);

	/**
	 * Strategy method called to obtain the <tt>base</tt> object to use when resolving {@link ELResolver#getValue
	 * values} and {@link ELResolver#getType types} from the {@link ELContext}.
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @return the base object. By default this implementation returns <tt>null</tt>
	 */
	protected Object getResolveBase(EvaluationContext context, Object target, String name) {
		return null;
	}

	/**
	 * Strategy method called to obtain the <tt>property</tt> object to use when resolving {@link ELResolver#getValue
	 * values} and {@link ELResolver#getType types} from the {@link ELContext}.
	 * @param context the evaluation context in which the access is being attempted
	 * @param target the target object upon which the property is being accessed
	 * @param name the name of the property being accessed
	 * @return the base object. By default this implementation returns <tt>name</tt>
	 */
	protected Object getResolveProperty(EvaluationContext context, Object target, String name) {
		return name;
	}

}
