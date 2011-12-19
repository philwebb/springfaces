package org.springframework.springfaces.showcase.validator;

import java.math.BigInteger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.bean.ForClass;
import org.springframework.springfaces.validator.Validator;
import org.springframework.stereotype.Component;

@Component
@ForClass
public class SpringBeanForClassValidator implements Validator<BigInteger> {

	public void validate(FacesContext context, UIComponent component, BigInteger value) throws ValidatorException {
		if (value.intValue() < 30) {
			throw new ValidatorException(new FacesMessage("Value must be 30 or more"));
		}
	}

}
