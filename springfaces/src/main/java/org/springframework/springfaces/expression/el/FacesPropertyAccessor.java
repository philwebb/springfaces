package org.springframework.springfaces.expression.el;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.PropertyAccessor;
import org.springframework.expression.EvaluationContext;

/**
 * {@link PropertyAccessor} that allows SPEL expressions to access properties against the current {@link FacesContext}.
 * Properties will only be resolved if the {@link FacesContext} is available, at all other times this property accessor
 * will be ignored.
 * 
 * @author Phillip Webb
 */
public class FacesPropertyAccessor extends ELPropertyAccessor {

	@Override
	protected ELContext getElContext(EvaluationContext context, Object target) {
		if (FacesContext.getCurrentInstance() != null) {
			return FacesContext.getCurrentInstance().getELContext();
		}
		return null;
	}
}