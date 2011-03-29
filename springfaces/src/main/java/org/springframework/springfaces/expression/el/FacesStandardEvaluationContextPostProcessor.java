package org.springframework.springfaces.expression.el;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.springfaces.expression.spel.support.StandardEvaluationContextPostProcessor;

/**
 * {@link StandardEvaluationContextPostProcessor} that allows SPEL expressions to access properties against the current
 * {@link FacesContext}. Properties will only be resolved if the {@link FacesContext} is available, at all other times
 * the property accessor will be ingnored.
 * 
 * @author Phillip Webb
 */
public class FacesStandardEvaluationContextPostProcessor implements StandardEvaluationContextPostProcessor {

	public void postProcessStandardEvaluationContext(StandardEvaluationContext evaluationContext) {
		evaluationContext.addPropertyAccessor(new FacesPropertyAccessor());
	}

	private static class FacesPropertyAccessor extends ELPropertyAccessor {

		@Override
		protected ELContext getElContext(EvaluationContext context, Object target) {
			if (FacesContext.getCurrentInstance() != null) {
				return FacesContext.getCurrentInstance().getELContext();
			}
			return null;
		}
	}

}
