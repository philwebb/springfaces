package org.springframework.springfaces.validator;

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link javax.faces.validator.Validator} that delegates to a Spring Bean.
 * 
 * @author Phillip Webb
 */
public class SpringBeanFacesValidator extends SpringBeanPartialStateHolder<javax.faces.validator.Validator> implements
		javax.faces.validator.Validator, FacesWrapper<javax.faces.validator.Validator> {

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanFacesValidator() {
		super();
	}

	/**
	 * Create a new {@link SpringBeanFacesValidator} instance.
	 * @param context the faces context
	 * @param beanName the bean name
	 */
	public SpringBeanFacesValidator(FacesContext context, String beanName) {
		super(context, beanName);
	}

	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		getBean().validate(context, component, value);
	}

	public javax.faces.validator.Validator getWrapped() {
		return getBean();
	}
}
