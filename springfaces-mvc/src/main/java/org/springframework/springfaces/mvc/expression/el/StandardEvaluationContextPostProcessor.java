package org.springframework.springfaces.mvc.expression.el;

import org.springframework.expression.spel.support.StandardEvaluationContext;

public interface StandardEvaluationContextPostProcessor {

	void postProcessStandardEvaluationContext(StandardEvaluationContext evaluationContext);

}
