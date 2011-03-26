package org.springframework.springfaces.mvc.expression.el;

import javax.el.ELContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.el.SpringBeanELResolver;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.springfaces.mvc.context.SpringFacesContext;

public class SpringBeanMvcELResolver extends SpringBeanELResolver {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	@Override
	protected BeanFactory getBeanFactory(ELContext elContext) {
		if (SpringFacesContext.getCurrentInstance() == null) {
			return EMPTY_BEAN_FACTORY;
		}
		// FIXME just becuase we have a context does not mean we are rendering
		// FIXME should we worry about stack overflow if bean references itself in a value?
		return SpringFacesContext.getCurrentInstance().getWebApplicationContext();
	}

}
