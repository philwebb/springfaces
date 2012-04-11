package org.springframework.springfaces.template.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.BeanValidator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.springframework.core.convert.Property;
import org.springframework.springfaces.expression.el.ELUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Default implementation of {@link ComponentInfo} that supports {@link EditableValueHolder}s as well as relevant
 * <tt>javax.validation.Validator</tt> annotations.
 * 
 * @author Phillip Webb
 */
public class DefaultComponentInfo implements ComponentInfo {

	private FacesContext context;

	private List<UIComponent> components;

	private BeanValidation beanValidation;

	public DefaultComponentInfo(FacesContext context, List<UIComponent> components) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(components, "Components must not be null");
		this.context = context;
		this.components = Collections.unmodifiableList(components);
		boolean hasBeanValidator = ClassUtils.isPresent("javax.validation.Validator", getClass().getClassLoader());
		this.beanValidation = hasBeanValidator ? new HasBeanValidation() : new NoBeanValidation();
	}

	public UIComponent getComponent() {
		if (this.components.isEmpty()) {
			return null;
		}
		return this.components.get(0);
	}

	public List<UIComponent> getComponents() {
		return this.components;
	}

	public boolean isValid() {
		for (UIComponent component : this.components) {
			if (!isValid(component)) {
				return false;
			}
		}
		return true;
	}

	private boolean isValid(UIComponent component) {
		if (component instanceof EditableValueHolder) {
			return isValid(component, (EditableValueHolder) component);
		}
		return false;
	}

	private boolean isValid(UIComponent component, EditableValueHolder editableValueHolder) {
		if (!editableValueHolder.isValid()) {
			return false;
		}
		String clientId = component.getClientId(this.context);
		return containsErrorMessage(this.context.getMessages(clientId));
	}

	private boolean containsErrorMessage(Iterator<FacesMessage> messages) {
		while (messages.hasNext()) {
			FacesMessage message = messages.next();
			if (message.getSeverity().getOrdinal() >= FacesMessage.SEVERITY_WARN.getOrdinal()) {
				return true;
			}
		}
		return false;
	}

	public boolean isRequired() {
		for (UIComponent component : this.components) {
			if (isRequired(component)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRequired(UIComponent component) {
		if (component instanceof EditableValueHolder) {
			return ((EditableValueHolder) component).isRequired();
		}
		return this.beanValidation.isRequired(component);
	}

	public String getLabel() {
		UIComponent component = getComponent();
		return (String) (component == null ? null : component.getAttributes().get("label"));
	}

	public String getFor() {
		UIComponent component = getComponent();
		return (component == null ? null : component.getClientId());
	}

	private interface BeanValidation {
		boolean isRequired(UIComponent component);
	}

	private class NoBeanValidation implements BeanValidation {
		public boolean isRequired(UIComponent component) {
			return false;
		}
	}

	private class HasBeanValidation implements BeanValidation {

		private Validator validator;

		public HasBeanValidation() {
			this.validator = getValidator();
		}

		private Validator getValidator() {
			Map<String, Object> applicationMap = DefaultComponentInfo.this.context.getExternalContext()
					.getApplicationMap();
			ValidatorFactory validatorFactory = getCachedValidatorFactory(applicationMap);
			if (validatorFactory == null) {
				validatorFactory = Validation.buildDefaultValidatorFactory();
				cacheValidatorFactory(applicationMap, validatorFactory);
			}
			return validatorFactory.getValidator();
		}

		private ValidatorFactory getCachedValidatorFactory(Map<String, Object> applicationMap) {
			Object cachedValue = applicationMap.get(BeanValidator.VALIDATOR_FACTORY_KEY);
			if (cachedValue != null && cachedValue instanceof ValidatorFactory) {
				return (ValidatorFactory) cachedValue;
			}
			return null;
		}

		private void cacheValidatorFactory(Map<String, Object> applicationMap, ValidatorFactory validatorFactory) {
			applicationMap.put(BeanValidator.VALIDATOR_FACTORY_KEY, validatorFactory);
		}

		public boolean isRequired(UIComponent component) {
			ELContext elContext = DefaultComponentInfo.this.context.getELContext();
			ValueExpression expression = component.getValueExpression("value");
			Property property = ELUtils.getProperty(expression, elContext);
			if (property == null) {
				return false;
			}
			BeanDescriptor beanConstraints = this.validator.getConstraintsForClass(property.getObjectType());
			PropertyDescriptor propertyConstraints = beanConstraints.getConstraintsForProperty(property.getName());
			return isRequired(propertyConstraints.getConstraintDescriptors());
		}

		private boolean isRequired(Set<ConstraintDescriptor<?>> descriptors) {
			for (ConstraintDescriptor<?> descriptor : descriptors) {
				if (ClassUtils.isAssignableValue(NotNull.class, descriptor.getAnnotation())) {
					return true;
				}
				if (isRequired(descriptor.getComposingConstraints())) {
					return true;
				}
			}
			return false;
		}
	}
}
