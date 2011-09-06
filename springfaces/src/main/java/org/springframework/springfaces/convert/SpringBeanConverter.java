package org.springframework.springfaces.convert;

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link Converter} that delegates to a Spring bean.
 * 
 * @author Phillip Webb
 */
public class SpringBeanConverter extends SpringBeanPartialStateHolder<Converter> implements Converter,
		FacesWrapper<Converter> {

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
		return getBean().getAsObject(context, component, value);
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return getBean().getAsString(context, component, value);
	}

	public Converter getWrapped() {
		return getBean();
	}
}
