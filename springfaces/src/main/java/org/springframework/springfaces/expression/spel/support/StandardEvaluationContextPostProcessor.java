/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
