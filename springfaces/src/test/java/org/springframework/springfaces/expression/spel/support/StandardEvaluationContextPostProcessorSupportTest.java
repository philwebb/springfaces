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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Tests for {@link StandardEvaluationContextPostProcessorSupport}.
 * 
 * @author Phillip Webb
 */
public class StandardEvaluationContextPostProcessorSupportTest {

	private static boolean postProcessCalled;

	@Test
	public void shouldCallStandardEvaluationContextPostProcessors() throws Exception {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("support", StandardEvaluationContextPostProcessorSupport.class);
		applicationContext.registerSingleton("postProcessor", PostProcessor.class);
		applicationContext.refresh();
		ConfigurableListableBeanFactory bf = applicationContext.getBeanFactory();
		BeanExpressionResolver resolver = bf.getBeanExpressionResolver();
		resolver.evaluate("#{support}", new BeanExpressionContext(bf, null));
		assertThat(postProcessCalled, is(true));

	}

	private static class PostProcessor implements StandardEvaluationContextPostProcessor {
		public void postProcessStandardEvaluationContext(StandardEvaluationContext evaluationContext) {
			postProcessCalled = true;
		}
	}

}
