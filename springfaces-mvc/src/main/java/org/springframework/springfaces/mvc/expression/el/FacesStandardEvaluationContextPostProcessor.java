package org.springframework.springfaces.mvc.expression.el;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
