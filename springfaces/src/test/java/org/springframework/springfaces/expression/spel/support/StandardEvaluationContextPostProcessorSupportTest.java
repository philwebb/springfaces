package org.springframework.springfaces.expression.spel.support;

import static org.junit.Assert.assertTrue;

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
		assertTrue(postProcessCalled);

	}

	private static class PostProcessor implements StandardEvaluationContextPostProcessor {
		public void postProcessStandardEvaluationContext(StandardEvaluationContext evaluationContext) {
			postProcessCalled = true;
		}
	}

}
