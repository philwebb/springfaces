package org.springframework.springfaces.convert;

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link Converter} that delegates to a {@link ConverterForClass} Spring bean.
 * 
 * @author Phillip Webb
 * 
 * @param <T>
 */
public class SpringBeanConverterForClass<T> extends SpringBeanPartialStateHolder<ConverterForClass<T>> implements
		Converter, FacesWrapper<ConverterForClass<T>> {

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanConverterForClass() {
		super();
	}

	/**
	 * Create a new {@link SpringBeanConverterForClass} instance.
	 * @param context the faces context
	 * @param beanName the bean name
	 */
	public SpringBeanConverterForClass(FacesContext context, String beanName) {
		super(context, beanName);
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return getBean().getAsObject(context, component, value);
	}

	@SuppressWarnings("unchecked")
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return getBean().getAsString(context, component, (T) value);
	}

	public ConverterForClass<T> getWrapped() {
		return getBean();
	}
}
