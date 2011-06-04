package org.springframework.springfaces.mvc;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * Mock {@link ELContext} for use with tests.
 * 
 * @author Phillip Webb
 */
public class MockELContext extends ELContext {

	@Override
	public ELResolver getELResolver() {
		return null;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return null;
	}

	@Override
	public VariableMapper getVariableMapper() {
		return null;
	}
}
