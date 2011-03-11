package org.springframework.springfaces;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class FacesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	//FIXME may not need

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames = beanFactory.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			Class<?> beanType = beanFactory.getType(beanName);
			if (beanType != null && beanType.equals(AnnotationMethodHandlerAdapter.class)) {
				BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
				bd.setBeanClassName(FacesAnnotationMethodHandlerAdapter.class.getName());
			}
		}
	}
}
