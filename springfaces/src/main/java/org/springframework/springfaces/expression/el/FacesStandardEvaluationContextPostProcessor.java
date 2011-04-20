package org.springframework.springfaces.expression.el;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.springfaces.expression.spel.support.StandardEvaluationContextPostProcessor;

/**
 * {@link StandardEvaluationContextPostProcessor} used to add a {@link FacesPropertyAccessor}.
 * 
 * @author Phillip Webb
 */
public class FacesStandardEvaluationContextPostProcessor implements StandardEvaluationContextPostProcessor {

	public void postProcessStandardEvaluationContext(StandardEvaluationContext evaluationContext) {
		evaluationContext.addPropertyAccessor(new FacesPropertyAccessor());
	}

}
