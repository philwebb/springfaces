package org.springframework.springfaces.validator;

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link javax.faces.validator.Validator} that delegates to a
 * {@link org.springframework.springfaces.validator.Validator} Spring Bean.
 * 
 * @param <T>
 * @author Phillip Webb
 */
public class SpringBeanValidator<T> extends
		SpringBeanPartialStateHolder<org.springframework.springfaces.validator.Validator<T>> implements
		javax.faces.validator.Validator, FacesWrapper<org.springframework.springfaces.validator.Validator<T>> {

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanValidator() {
		super();
	}

	/**
	 * Create a new {@link SpringBeanValidator} instance.
	 * @param context the faces context
	 * @param beanName the bean name
	 */
	public SpringBeanValidator(FacesContext context, String beanName) {
		super(context, beanName);
	}

	@SuppressWarnings("unchecked")
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		getBean().validate(context, component, (T) value);
	}

	public org.springframework.springfaces.validator.Validator<T> getWrapped() {
		return getBean();
	}
}
