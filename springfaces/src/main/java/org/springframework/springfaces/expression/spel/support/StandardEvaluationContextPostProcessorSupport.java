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
			beanFactory.setBeanExpressionResolver(new PostProcessorAwareStandardBeanExpressionResolver());
		}
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext ctx = event.getApplicationContext();
		this.postProcessors = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx,
				StandardEvaluationContextPostProcessor.class).values();
	}

	/**
	 * Strategy method used to determine of a {@link BeanExpressionResolver} can be replaced with a
	 * {@link StandardEvaluationContextPostProcessor} aware variant. By default only
	 * {@link StandardBeanExpressionResolver}s can be replaced.
	 * @param beanExpressionResolver the bean expression resolver being considered
	 * @return <tt>true</tt> if the resolver can be replaced
	 */
	protected boolean isReplaceable(BeanExpressionResolver beanExpressionResolver) {
		return beanExpressionResolver.getClass().equals(StandardBeanExpressionResolver.class);
	}

	private class PostProcessorAwareStandardBeanExpressionResolver extends StandardBeanExpressionResolver {
		@Override
		protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
			super.customizeEvaluationContext(evalContext);
			if (StandardEvaluationContextPostProcessorSupport.this.postProcessors != null) {
				for (StandardEvaluationContextPostProcessor postProcessor : StandardEvaluationContextPostProcessorSupport.this.postProcessors) {
					postProcessor.postProcessStandardEvaluationContext(evalContext);
				}
			}
		}
	}
}
