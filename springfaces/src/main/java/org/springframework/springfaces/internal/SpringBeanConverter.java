package org.springframework.springfaces.internal;

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link javax.faces.convert.Converter} that delegates to a
 * {@link org.springframework.springfaces.convert.Converter} Spring bean.
 * 
 * @author Phillip Webb
 * 
 * @param <T>
 */
public class SpringBeanConverter<T> extends
		SpringBeanPartialStateHolder<org.springframework.springfaces.convert.Converter<T>> implements
		javax.faces.convert.Converter, FacesWrapper<org.springframework.springfaces.convert.Converter<T>> {

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanConverter() {
		super();
	}

	/**
	 * Create a new {@link SpringBeanConverter} instance.
	 * @param context the faces context
	 * @param beanName the bean name
	 */
	public SpringBeanConverter(FacesContext context, String beanName) {
		super(context, beanName);
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// FIXME type check
		return getBean().getAsObject(context, component, value);
	}

	@SuppressWarnings("unchecked")
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		// FIXME type check
		return getBean().getAsString(context, component, (T) value);
	}

	public org.springframework.springfaces.convert.Converter<T> getWrapped() {
		return getBean();
	}
}
