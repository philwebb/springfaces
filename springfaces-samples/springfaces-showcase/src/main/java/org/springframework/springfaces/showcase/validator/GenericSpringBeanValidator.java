package org.springframework.springfaces.showcase.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class GenericSpringBeanValidator implements Validator<Integer> {

	public void validate(FacesContext context, UIComponent component, Integer value) throws ValidatorException {
		if (value < 10) {
			throw new ValidatorException(new FacesMessage("Value must be 10 or more"));
		}
	}

}
