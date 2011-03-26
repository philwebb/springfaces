package org.springframework.springfaces.mvc.expression.el;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * A {@link BeanFactoryPostProcessor} that can be registered to with an application context in order to support
 * {@link StandardEvaluationContextPostProcessor}s.
 * 
 * @author Phillip Webb
 */
public class StandardEvaluationContextPostProcessorSupport implements BeanFactoryPostProcessor,
		ApplicationListener<ContextRefreshedEvent> {

	private Collection<? extends StandardEvaluationContextPostProcessor> postProcessors;

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanExpressionResolver beanExpressionResolver = beanFactory.getBeanExpressionResolver();
		if (beanExpressionResolver != null) {
			Assert.state(isReplaceable(beanExpressionResolver), "Unable to replace beanExpressionResolver "
					+ beanExpressionResolver.getClass());
			beanFactory.setBeanExpressionResolver(new FacesStandardBeanExpressionResolver());
		}
	}

	protected boolean isReplaceable(BeanExpressionResolver beanExpressionResolver) {
		return beanExpressionResolver.getClass().equals(StandardBeanExpressionResolver.class);
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext ctx = event.getApplicationContext();
		postProcessors = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx,
				StandardEvaluationContextPostProcessor.class).values();
	}

	private class FacesStandardBeanExpressionResolver extends StandardBeanExpressionResolver {

		@Override
		protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
			super.customizeEvaluationContext(evalContext);
			if (postProcessors != null) {
				for (StandardEvaluationContextPostProcessor postProcessor : postProcessors) {
					postProcessor.postProcessStandardEvaluationContext(evalContext);
				}
			}
		}
	}
}
