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

import java.util.Locale;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.springframework.util.Assert;

/**
 * Base for an {@link ELContext} <tt>Decorator</tt>.
 * 
 * @author Phillip Webb
 */
public abstract class ELContextDecorator extends ELContext {

	private ELContext elContext;

	public ELContextDecorator(ELContext elContext) {
		Assert.notNull(elContext, "ELContext must not be null");
		this.elContext = elContext;
	}

	@Override
	public void setPropertyResolved(boolean resolved) {
		this.elContext.setPropertyResolved(resolved);
	}

	@Override
	public boolean isPropertyResolved() {
		return this.elContext.isPropertyResolved();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void putContext(Class key, Object contextObject) {
		this.elContext.putContext(key, contextObject);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getContext(Class key) {
		return this.elContext.getContext(key);
	}

	@Override
	public Locale getLocale() {
		return this.elContext.getLocale();
	}

	@Override
	public void setLocale(Locale locale) {
		this.elContext.setLocale(locale);
	}

	@Override
	public ELResolver getELResolver() {
		return this.elContext.getELResolver();
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return this.elContext.getFunctionMapper();
	}

	@Override
	public VariableMapper getVariableMapper() {
		return this.elContext.getVariableMapper();
	}
}
