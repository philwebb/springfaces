package org.springframework.springfaces.convert;

import java.util.HashMap;
import java.util.Map;

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

public class SpringFacesConverterSupport implements FacesWrapperFactory<Application>,
		ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

	private static final Class<?> FACES_CONVERTER_TYPE = javax.faces.convert.Converter.class;
	private static final Class<?> CONVERTER_TYPE = org.springframework.springfaces.convert.Converter.class;

	private ApplicationContext applicationContext;

	private HashMap<String, Object> converters;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		if (applicationContext == this.applicationContext) {
			this.converters = new HashMap<String, Object>();
			this.converters.putAll(beansOfType(applicationContext, FACES_CONVERTER_TYPE));
			this.converters.putAll(beansOfType(applicationContext, CONVERTER_TYPE));
		}
	}

	private <T> Map<String, T> beansOfType(ApplicationContext applicationContext, Class<T> type) {
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, type, true, true);
	}

	public HashMap<String, Object> getConverters() {
		return converters;
	}

	public Application newWrapper(Class<?> typeClass, Application delegate) {
		return new ConverterApplication(delegate);
	}

	public class ConverterApplication extends ApplicationWrapper {

		private Application wrapped;

		public ConverterApplication(Application wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public Converter createConverter(Class<?> targetClass) {
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

		private Converter createConverterBean(String converterId) {
			Object bean = converters.get(converterId);
			if (bean != null) {
				if (CONVERTER_TYPE.isInstance(bean)) {
					return new SpringBeanConverter(FacesContext.getCurrentInstance(), converterId);
				}
				if (FACES_CONVERTER_TYPE.isInstance(bean)) {
					return new SpringBeanFacesConverter(FacesContext.getCurrentInstance(), converterId);
				}
			}
			return null;
		}

		@Override
		public Application getWrapped() {
			return wrapped;
		}
	}
}
