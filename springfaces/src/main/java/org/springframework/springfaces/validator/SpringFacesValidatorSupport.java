package org.springframework.springfaces.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.bean.ConditionalForClass;
import org.springframework.springfaces.bean.ForClass;
import org.springframework.springfaces.component.SpringBeanPartialStateHolder;
import org.springframework.springfaces.util.ForClassFilter;

/**
 * {@link FacesWrapperFactory} for JSF {@link Application}s that offers extended Spring validator support. All Spring
 * Beans that implement {@link javax.faces.validator.Validator} or
 * {@link org.springframework.springfaces.validator.Validator} are made available as JSF validators (the ID of the bean
 * is used as the validator name). The {@link ForClass @ForClass} annotation and {@link ConditionalForClass} interface
 * are also supported to return default validators for a class.
 * 
 * @author Phillip Webb
 */
public class SpringFacesValidatorSupport implements FacesWrapperFactory<Application>,
		ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware, BeanNameAware {

	private static final Class<?> FACES_VALIDATOR_TYPE = javax.faces.validator.Validator.class;
	private static final Class<?> VALIDATOR_TYPE = org.springframework.springfaces.validator.Validator.class;

	private static final ForClassFilter FOR_CLASS_FILTER = new ForClassFilter(VALIDATOR_TYPE);

	private ApplicationContext applicationContext;

	private String beanName;

	private HashMap<String, Object> validators;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setBeanName(String name) {
		this.beanName = name;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		if (applicationContext == this.applicationContext) {
			collectValidatorBeans();
		}
	}

	/**
	 * Collects all relevant spring beans to the validators map.
	 */
	private void collectValidatorBeans() {
		this.validators = new HashMap<String, Object>();
		this.validators.putAll(beansOfType(applicationContext, FACES_VALIDATOR_TYPE));
		this.validators.putAll(beansOfType(applicationContext, VALIDATOR_TYPE));
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

	Validator createValidatorBean(Class<?> targetClass) {
		Set<String> beanIds = FOR_CLASS_FILTER.apply(validators, targetClass).keySet();
		if (beanIds.isEmpty()) {
			return null;
		}
		if (beanIds.size() != 1) {
			throw new IllegalStateException("Multiple JSF converters registered with Spring for "
					+ targetClass.getName() + " : " + beanIds);
		}
		return createValidatorBean(beanIds.iterator().next());
	}

	private Validator createValidatorBean(String beanId) {
		Object bean = validators.get(beanId);
		if (bean != null) {
			if (VALIDATOR_TYPE.isInstance(bean)) {
				return new SpringBeanValidator(FacesContext.getCurrentInstance(), beanId);
			}
			if (FACES_VALIDATOR_TYPE.isInstance(bean)) {
				return new SpringBeanFacesValidator(FacesContext.getCurrentInstance(), beanId);
			}
		}
		return null;
	}

	public Application newWrapper(Class<?> typeClass, Application delegate) {
		return new ValidatorApplication(delegate);
	}

	/**
	 * {@link Application} wrapper that offers extended Spring validator support.
	 */
	public class ValidatorApplication extends ApplicationWrapper {

		private Application wrapped;

		public ValidatorApplication(Application wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public Map<String, String> getDefaultValidatorInfo() {
			return super.getDefaultValidatorInfo();
		}

		@Override
		public Validator createValidator(String validatorId) throws FacesException {
			Validator validator = createValidatorBean(validatorId);
			if (validator != null) {
				return validator;
			}
			return super.createValidator(validatorId);
		}

		@Override
		public Application getWrapped() {
			return wrapped;
		}
	}

	public static class DefaultValidator extends SpringBeanPartialStateHolder<SpringFacesValidatorSupport> implements
			Validator {

		@Deprecated
		public DefaultValidator() {
			super();
		}

		public DefaultValidator(FacesContext context, String beanName) {
			super(context, beanName);
		}

		public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
			// FIXME do we allow validation of null value
			if (value == null) {
				return;
			}
			Class<?> targetClass = value.getClass();
			Validator validatorBean = getBean().createValidatorBean(targetClass);
			if (validatorBean != null) {
				validatorBean.validate(context, component, value);
			}
		}

	}

}
