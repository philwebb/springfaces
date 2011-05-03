package org.springframework.springfaces.mvc.expression.el;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.el.SpringBeanELResolver;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.springfaces.mvc.context.SpringFacesContext;

/**
 * Unified EL {@link ELResolver} that delegates to the appropriate {@link SpringFacesContext#getWebApplicationContext()
 * BeanFactory} when a {@link SpringFacesContext} is active and rendering a view.
 * 
 * @author Phillip Webb
 */
public class SpringBeanMvcELResolver extends SpringBeanELResolver {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	@Override
	protected BeanFactory getBeanFactory(ELContext elContext) {
		if (SpringFacesContext.getCurrentInstance() == null
				|| SpringFacesContext.getCurrentInstance().getRendering() == null) {
			return EMPTY_BEAN_FACTORY;
		}
		return SpringFacesContext.getCurrentInstance().getWebApplicationContext();
	}

}
