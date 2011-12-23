package org.springframework.springfaces.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.component.StateHolder;
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
import org.springframework.springfaces.util.StateHolders;

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
		this.validators.putAll(beansOfType(this.applicationContext, FACES_VALIDATOR_TYPE));
		this.validators.putAll(beansOfType(this.applicationContext, VALIDATOR_TYPE));
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

	protected final Collection<Validator> createValidatorBeans(Class<?> targetClass) {
		Set<String> beanIds = FOR_CLASS_FILTER.apply(this.validators, targetClass).keySet();
		List<Validator> validators = new ArrayList<Validator>();
		for (String beanId : beanIds) {
			validators.add(createValidatorBean(beanId));
		}
		return Collections.unmodifiableList(validators);
	}

	private Validator createValidatorBean(String beanId) {
		Object bean = this.validators.get(beanId);
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
			wrapped.addValidator(DefaultValidator.VALIDATOR_ID, DefaultValidator.class.getName());
			wrapped.addDefaultValidatorId(DefaultValidator.VALIDATOR_ID);
		}

		@Override
		public Validator createValidator(String validatorId) throws FacesException {
			if (DefaultValidator.VALIDATOR_ID.equals(validatorId)) {
				return new DefaultValidator(FacesContext.getCurrentInstance(),
						SpringFacesValidatorSupport.this.beanName);
			}
			Validator validator = createValidatorBean(validatorId);
			if (validator != null) {
				return validator;
			}
			return super.createValidator(validatorId);
		}

		@Override
		public Application getWrapped() {
			return this.wrapped;
		}
	}

	/**
	 * A {@link Application#addDefaultValidatorId(String) default} {@link Validator} that supports {@link ForClass} and
	 * {@link ConditionalForClass} beans.
	 */
	public static class DefaultValidator extends SpringBeanPartialStateHolder<SpringFacesValidatorSupport> implements
			Validator {

		static String VALIDATOR_ID = "org.springframework.validator.default";

		private StateHolders<StateHolder> validators;

		/**
		 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
		 * @deprecated use alternative constructor
		 */
		@Deprecated
		public DefaultValidator() {
			super();
		}

		/**
		 * Create a new {@link DefaultValidator} implementation.
		 * @param context the faces context
		 * @param springFacesValidatorSupportBeanName the name of the {@link SpringFacesValidatorSupport} bean
		 */
		public DefaultValidator(FacesContext context, String springFacesValidatorSupportBeanName) {
			super(context, springFacesValidatorSupportBeanName);
		}

		public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
			if (value == null) {
				return;
			}
			Class<?> targetClass = value.getClass();
			Collection<Validator> validators = getValidators(targetClass);
			for (Validator validator : validators) {
				validator.validate(context, component, value);
			}
		}

		private Collection<Validator> getValidators(Class<?> targetClass) {
			if (this.validators == null) {
				this.validators = new StateHolders<StateHolder>();
				Collection<Validator> validatorBeans = getBean().createValidatorBeans(targetClass);
				for (Validator validator : validatorBeans) {
					this.validators.add((StateHolder) validator);
				}
			}
			return asValidators(this.validators.asList());
		}

		@SuppressWarnings("unchecked")
		private List<Validator> asValidators(List<StateHolder> stateHolders) {
			return (List) stateHolders;
		}
	}

}
