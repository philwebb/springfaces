package org.springframework.springfaces.mvc.bind;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.ConvertingPropertyEditorAdapter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

/**
 * Utility class that can be used to perform a reverse bind for a given {@link DataBinder}. This class can be used to
 * obtain {@link PropertyValues} for a given a {@link DataBinder} based on the current values of its <tt>target</tt> or
 * perform a simple reverse conversion for plain parameter values when the binders <tt>target</tt> is <tt>null</tt>.
 * 
 * @author Phillip Webb
 */
public class ReverseDataBinder {

	Log logger = LogFactory.getLog(getClass());

	/**
	 * Set of properties that are always skipped.
	 */
	private static final Set<String> SKIPPED_PROPERTIES;
	static {
		SKIPPED_PROPERTIES = new HashSet<String>();
		SKIPPED_PROPERTIES.add("class");
	}

	private DataBinder dataBinder;

	private SimpleTypeConverter simpleTypeConverter;

	private boolean skipDefaultValues = true;

	/**
	 * Default constructor.
	 * @param dataBinder a non null dataBinder
	 */
	public ReverseDataBinder(DataBinder dataBinder) {
		Assert.notNull(dataBinder, "DataBinder must not be null");
		this.dataBinder = dataBinder;
	}

	/**
	 * Reverse convert a simple object value.
	 * @param value the value to convert
	 * @return the converted value
	 */
	public String reverseConvert(Object value) {
		if (value == null) {
			return null;
		}
		PropertyEditor propertyEditor = findEditor(null, null, null, value.getClass(), TypeDescriptor.forObject(value));
		return convertToStringUsingPropertyEditor(value, propertyEditor);
	}

	/**
	 * Perform the reverse bind on the <tt>dataBinder</tt> provided in the constructor. Note: Calling with method will
	 * also trigger a <tt>bind</tt> operation on the <tt>dataBinder</tt>. This method returns {@link PropertyValues}
	 * containing a name/value pairs for each property that can be bound. Property values are encoded as Strings using
	 * the property editors bound to the original dataBinder.
	 * @return property values that could be re-bound using the data binder
	 * @throws IllegalStateException if the target object values cannot be bound
	 */
	public PropertyValues reverseBind() {
		Assert.notNull(this.dataBinder.getTarget(),
				"ReverseDataBinder.reverseBind can only be used with a DataBinder that has a target object");

		MutablePropertyValues rtn = new MutablePropertyValues();
		BeanWrapper target = PropertyAccessorFactory.forBeanPropertyAccess(this.dataBinder.getTarget());

		ConversionService conversionService = this.dataBinder.getConversionService();
		if (conversionService != null) {
			target.setConversionService(conversionService);
		}

		PropertyDescriptor[] propertyDescriptors = target.getPropertyDescriptors();

		BeanWrapper defaultValues = null;
		if (this.skipDefaultValues) {
			defaultValues = newDefaultTargetValues(this.dataBinder.getTarget());
		}

		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor property = propertyDescriptors[i];
			String propertyName = PropertyAccessorUtils.canonicalPropertyName(property.getName());
			Object propertyValue = target.getPropertyValue(propertyName);

			if (isSkippedProperty(property)) {
				continue;
			}

			if (!isMutableProperty(property)) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Ignoring '" + propertyName + "' due to missing read/write methods");
				}
				continue;
			}

			if (defaultValues != null
					&& ObjectUtils.nullSafeEquals(defaultValues.getPropertyValue(propertyName), propertyValue)) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Skipping '" + propertyName + "' as property contains default value");
				}
				continue;
			}

			// Find a property editor
			PropertyEditorRegistrySupport propertyEditorRegistrySupport = null;
			if (target instanceof PropertyEditorRegistrySupport) {
				propertyEditorRegistrySupport = (PropertyEditorRegistrySupport) target;
			}

			PropertyEditor propertyEditor = findEditor(propertyName, propertyEditorRegistrySupport,
					target.getWrappedInstance(), target.getPropertyType(propertyName),
					target.getPropertyTypeDescriptor(propertyName));

			// Convert and store the value
			String convertedPropertyValue = convertToStringUsingPropertyEditor(propertyValue, propertyEditor);
			if (convertedPropertyValue != null) {
				rtn.addPropertyValue(propertyName, convertedPropertyValue);
			}
		}

		this.dataBinder.bind(rtn);
		BindingResult bindingResult = this.dataBinder.getBindingResult();
		if (bindingResult.hasErrors()) {
			throw new IllegalStateException("Unable to reverse bind from target '" + this.dataBinder.getObjectName()
					+ "', the properties '" + rtn + "' will result in binding errors when re-bound "
					+ bindingResult.getAllErrors());
		}
		return rtn;
	}

	/**
	 * Find a property editor by searching custom editors or falling back to default editors.
	 * @param propertyName the property name or <tt>null</tt> if looking for an editor for all properties of the given
	 * type
	 * @param propertyEditorRegistrySupport an optional {@link PropertyEditorRegistrySupport} instance. If <tt>null</tt>
	 * a {@link SimpleTypeConverter} instance will be used
	 * @param targetObject the target object or <tt>null</tt>
	 * @param requiredType the required type.
	 * @param typeDescriptor the type descriptor
	 * @return the corresponding editor, or <code>null</code> if none
	 */
	protected PropertyEditor findEditor(String propertyName,
			PropertyEditorRegistrySupport propertyEditorRegistrySupport, Object targetObject, Class<?> requiredType,
			TypeDescriptor typeDescriptor) {

		Assert.notNull(requiredType, "RequiredType must not be null");
		Assert.notNull(typeDescriptor, "TypeDescription must not be null");

		// Use the custom editor if there is one
		PropertyEditor editor = this.dataBinder.findCustomEditor(requiredType, propertyName);
		if (editor != null) {
			return editor;
		}

		// Use the conversion service
		ConversionService conversionService = this.dataBinder.getConversionService();
		if (conversionService != null) {
			if (conversionService.canConvert(TypeDescriptor.valueOf(String.class), typeDescriptor)) {
				return new ConvertingPropertyEditorAdapter(conversionService, typeDescriptor);
			}
		}

		// Fall back to default editors
		if (propertyEditorRegistrySupport == null) {
			propertyEditorRegistrySupport = getSimpleTypeConverter();
		}
		return findDefaultEditor(propertyEditorRegistrySupport, targetObject, requiredType, typeDescriptor);
	}

	/**
	 * Gets the {@link SimpleTypeConverter} that should be used for conversion.
	 * @return the simple type converter
	 */
	protected SimpleTypeConverter getSimpleTypeConverter() {
		if (this.simpleTypeConverter == null) {
			this.simpleTypeConverter = new SimpleTypeConverter();
		}
		return this.simpleTypeConverter;
	}

	/**
	 * Find a default editor for the given type. This code is based on <tt>TypeConverterDelegate.findDefaultEditor</tt>.
	 * @param requiredType the type to find an editor for
	 * @param typeDescriptor the type description of the property
	 * @return the corresponding editor, or <code>null</code> if none
	 * 
	 * @param propertyEditorRegistry
	 * @param targetObject
	 * 
	 * @author Juergen Hoeller
	 * @author Rob Harrop
	 */
	protected PropertyEditor findDefaultEditor(PropertyEditorRegistrySupport propertyEditorRegistry,
			Object targetObject, Class<?> requiredType, TypeDescriptor typeDescriptor) {
		PropertyEditor editor = null;
		// FIXME check 3.1 final to see if this is still commented
		// if (typeDescriptor instanceof PropertyTypeDescriptor) {
		// PropertyDescriptor pd = ((PropertyTypeDescriptor) typeDescriptor).getPropertyDescriptor();
		// editor = pd.createPropertyEditor(targetObject);
		// }
		if (editor == null && requiredType != null) {
			// No custom editor -> check BeanWrapperImpl's default editors.
			editor = propertyEditorRegistry.getDefaultEditor(requiredType);
			if (editor == null && !String.class.equals(requiredType)) {
				// No BeanWrapper default editor -> check standard JavaBean editor.
				editor = BeanUtils.findEditorByConvention(requiredType);
			}
		}
		return editor;
	}

	/**
	 * Utility method to convert a given value into a string using a property editor.
	 * @param value the value to convert (can be <tt>null</tt>)
	 * @param propertyEditor the property editor or <tt>null</tt> if no suitable property editor exists
	 * @return the converted value
	 */
	private String convertToStringUsingPropertyEditor(Object value, PropertyEditor propertyEditor) {
		if (propertyEditor != null) {
			propertyEditor.setValue(value);
			return propertyEditor.getAsText();
		}
		if (value instanceof String) {
			return value == null ? null : value.toString();
		}
		return null;
	}

	private BeanWrapper newDefaultTargetValues(Object target) {
		try {
			Object defaultValues = target.getClass().newInstance();
			return PropertyAccessorFactory.forBeanPropertyAccess(defaultValues);
		} catch (Exception e) {
			this.logger.warn("Unable to construct default values target instance for class " + target.getClass()
					+ ", default values will not be skipped");
			return null;
		}
	}

	/**
	 * Determine if a property should be skipped. Used to ignore object properties.
	 * @param property the property descriptor
	 * @return <tt>true</tt> if the property is skipped
	 */
	private boolean isSkippedProperty(PropertyDescriptor property) {
		return SKIPPED_PROPERTIES.contains(property.getName());
	}

	/**
	 * Determine if a property contains both read and write methods.
	 * @param descriptor the property descriptor
	 * @return <tt>true</tt> if the property is mutable
	 */
	private boolean isMutableProperty(PropertyDescriptor descriptor) {
		return descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null;
	}

	/**
	 * Skip any bound values when the current value is identical to the value of a newly constructed instance. This
	 * setting can help to reduce the number of superfluous bound properties. Note: If the target object class does not
	 * have a default (no-args) constructor this setting will be ignored. The default setting is <tt>true</tt>.
	 * @param skipDefaultValues <tt>true</tt> if default properties should be ignored, otherwise <tt>false</tt>
	 */
	public void setSkipDefaultValues(boolean skipDefaultValues) {
		this.skipDefaultValues = skipDefaultValues;
	}
}
