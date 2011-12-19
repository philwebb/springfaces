package org.springframework.springfaces.showcase.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.stereotype.Component;

@Component
public class SpringBeanValidator implements Validator {

	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if ((Integer) value < 20) {
			throw new ValidatorException(new FacesMessage("Value must be 20 or more"));
		}
	}

}
