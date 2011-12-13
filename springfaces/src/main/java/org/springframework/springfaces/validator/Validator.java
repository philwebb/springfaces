package org.springframework.springfaces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * A variation of the JSF {@link javax.faces.validator.Validator} that support generic typing.
 * 
 * @param <T> The type the validator is for.
 * 
 * @author Phillip Webb
 */
public interface Validator<T> {

	/**
	 * See {@link javax.faces.validator.Validator#validate(FacesContext, UIComponent, Object)}.
	 * @param context the faces context
	 * @param component the source component
	 * @param value the value to validate
	 * @throws ValidatorException
	 */
	public void validate(FacesContext context, UIComponent component, T value) throws ValidatorException;

}
