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
	public void putContext(Class key, Object contextObject) {
		this.elContext.putContext(key, contextObject);
	}

	@Override
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
