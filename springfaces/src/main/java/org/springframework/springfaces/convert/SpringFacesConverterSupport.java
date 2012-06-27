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
package org.springframework.springfaces.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.bean.ConditionalForClass;
import org.springframework.springfaces.bean.ForClass;
import org.springframework.springfaces.util.ForClassFilter;

/**
 * {@link FacesWrapperFactory} for JSF {@link Application}s that offers extended Spring converter support. All Spring
 * Beans that implement {@link javax.faces.convert.Converter} or
 * {@link org.springframework.springfaces.convert.Converter} are made available as JSF converters (the ID of the bean is
 * used as the converter name). The {@link ForClass @ForClass} annotation and {@link ConditionalForClass} interface are
 * also supported to return default converters for a class.
 * @author Phillip Webb
 */
public class SpringFacesConverterSupport implements FacesWrapperFactory<Application>,
		ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

	private static final Class<?> FACES_CONVERTER_TYPE = javax.faces.convert.Converter.class;
	private static final Class<?> CONVERTER_TYPE = org.springframework.springfaces.convert.Converter.class;

	private static final ForClassFilter FOR_CLASS_FILTER = new ForClassFilter(CONVERTER_TYPE);

	private ApplicationContext applicationContext;

	private HashMap<String, Object> converters;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		if (applicationContext == this.applicationContext) {
			collectConverterBeans();
		}
	}

	/**
	 * Collects all relevant spring beans to the converters map.
	 */
	private void collectConverterBeans() {
		this.converters = new HashMap<String, Object>();
		this.converters.putAll(beansOfType(this.applicationContext, FACES_CONVERTER_TYPE));
		this.converters.putAll(beansOfType(this.applicationContext, CONVERTER_TYPE));
	}

	/**
	 * Returns all beans from the application context of the specified type.
	 * @param applicationContext the application context
	 * @param type the type of beans to return
	 * @return a map of bean name to bean instance
	 */
	private <T> Map<String, T> beansOfType(ApplicationContext applicationContext, Class<T> type) {
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, type, true, true);
	}

	public Application newWrapper(Class<?> typeClass, Application wrapped) {
		return new ConverterApplication(wrapped);
	}

	/**
	 * Creates a converter from a Spring bean for the given target class or returns <tt>null</tt> if no suitable bean is
	 * found.
	 * @param targetClass the target class
	 * @return a converter or <tt>null</tt>
	 */
	private Converter createConverterBean(Class<?> targetClass) {
		Set<String> beanIds = FOR_CLASS_FILTER.apply(this.converters, targetClass).keySet();
		if (beanIds.isEmpty()) {
			return null;
		}
		if (beanIds.size() != 1) {
			throw new IllegalStateException("Multiple JSF converters registered with Spring for "
					+ targetClass.getName() + " : " + beanIds);
		}
		return createConverterBean(beanIds.iterator().next());
	}

	/**
	 * Creates a converter from a Spring bean for the given bean ID or returns <tt>null</tt> if no bean is found.
	 * @param beanId the bean ID
	 * @return a converter or <tt>null</tt>
	 */
	private Converter createConverterBean(String beanId) {
		Object bean = this.converters.get(beanId);
		if (bean != null) {
			if (CONVERTER_TYPE.isInstance(bean)) {
				return new SpringBeanConverter<Converter>(FacesContext.getCurrentInstance(), beanId);
			}
			if (FACES_CONVERTER_TYPE.isInstance(bean)) {
				return new SpringBeanFacesConverter(FacesContext.getCurrentInstance(), beanId);
			}
		}
		return null;
	}

	/**
	 * {@link Application} wrapper that offers extended Spring converter support.
	 */
	public class ConverterApplication extends ApplicationWrapper {

		private Application wrapped;

		public ConverterApplication(Application wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public Application getWrapped() {
			return this.wrapped;
		}

		@Override
		public Converter createConverter(Class<?> targetClass) {
			Converter converter = createConverterBean(targetClass);
			if (converter != null) {
				return converter;
			}
			return super.createConverter(targetClass);
		}

		@Override
		public Converter createConverter(String converterId) {
			Converter converter = createConverterBean(converterId);
			if (converter != null) {
				return converter;
			}
			return super.createConverter(converterId);
		}
	}
}
