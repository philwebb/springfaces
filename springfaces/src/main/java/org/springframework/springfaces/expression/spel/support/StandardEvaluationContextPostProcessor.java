package org.springframework.springfaces.expression.spel.support;

import org.springframework.beans.PropertyAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Factory hook that allows for custom modification of {@link StandardEvaluationContext}s, e.g. to
 * {@link StandardEvaluationContext#addPropertyAccessor add} {@link PropertyAccessor}s.
 * <p>
 * In order for an {@link ApplicationContext} to auto-detect StandardEvaluationContextPostProcessors the
 * {@link StandardEvaluationContextPostProcessorSupport} bean must also be registered.
 * 
 * @see StandardEvaluationContextPostProcessorSupport
 * 
 * @author Phillip Webb
 */
public interface StandardEvaluationContextPostProcessor {

	/**
	 * Post processes a {@link StandardEvaluationContext}. This method will be called after the context has been created
	 * but before it has resolved any expressions. Generally implementations will add {@link ConstructorResolver}s,
	 * {@link MethodResolver}s or {@link PropertyAccessor}s to the context.
	 * @param evaluationContext the evaluation context
	 */
	void postProcessStandardEvaluationContext(StandardEvaluationContext evaluationContext);

}
