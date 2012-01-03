package org.springframework.springfaces.selectitems.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.expression.el.ELUtils;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.message.ObjectMessageSourceUtils;
import org.springframework.springfaces.selectitems.SelectItemsConverter;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;

public class UISelectItems extends UIComponentBase {

	// FIXME make a complete component

	// - Drop in replacement for f:selectItems
	// - Attaches converter if there is not one
	// - Can use an expression to create item ID (getAsString)
	// - Generates select items from the bound value (Enums, Booleans, Others?)
	// - Allows a noSelection item to be inserted easily
	//
	// <s:selectItems
	// value - optional, if not specified will generate items from the bound parent component value
	// var - The var, optional will default to item
	// itemValue - Optional default id item from collection
	// itemLabel - As standard, optional default JSF conversion of value
	// itemEscaped - Optional
	// itemDescription - Optional default null
	// itemDisabled - Optional default false
	// itemConvertedValue - The converted to string value, optional defaults to either @Id (if one) or value.toString()
	// includeNoSelection - Includes a noSelection option, defaults to false
	// Do we want a noSelectionValue? What if the converted item is a no selection?
	public static final String COMPONENT_FAMILY = "spring.faces.SelectItems";

	private ExposedUISelectItems exposedUISelectItems = new ExposedUISelectItems();

	private UISelectItemsConverter converter = new UISelectItemsConverter();

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	protected Collection<SelectItem> getSelectItems() {
		Collection<SelectItem> selectItems = new ArrayList<SelectItem>();
		Collection<Object> valueItems = getValueItems();
		for (Object valueItem : valueItems) {
			SelectItem selectItem = convertToSelectItem(valueItem);
			selectItems.add(selectItem);
		}
		return selectItems;
	}

	private Collection<Object> getValueItems() {

		// FIXME
		Object value = getValue();
		if (value == null) {
			return getValueItemsForParentComponentValueExpression();
		}
		if (value instanceof Collection) {
			return (Collection) value;
		}
		if (value instanceof Array) {
			return Arrays.asList((Object[]) value);
		}
		// FIXME String to class lookup?
		// FIXME String array?
		return null;
	}

	private Collection<Object> getValueItemsForParentComponentValueExpression() {
		ValueExpression valueExpression = getParent().getValueExpression("value");
		TypeDescriptor typeDescriptor = ELUtils.getTypeDescriptor(valueExpression, getFacesContext().getELContext());
		System.out.println(typeDescriptor);

		//
		//
		// TrackingELContext elContext = new TrackingELContext(getFacesContext().getELContext());
		// Class<?> expectedType = valueExpression.getType(elContext);
		if (Enum.class.isAssignableFrom(typeDescriptor.getType())) {
			return EnumSet.allOf((Class) typeDescriptor.getType());
		}
		// Entry entry = elContext.getEntries().get(0);
		// Class<?> clazz = entry.getBase().getClass();
		// PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, entry.getProperty().toString());
		// Property property = new Property(clazz, pd.getReadMethod(), pd.getWriteMethod());
		// TypeDescriptor typeDescriptor = new TypeDescriptor(property);
		// System.out.println(typeDescriptor);
		// System.out.println(typeDescriptor.getElementTypeDescriptor());

		// FIXME
		return Arrays.<Object> asList("a", "b", "c");
	}

	private SelectItem convertToSelectItem(final Object valueItem) {
		if (valueItem instanceof SelectItem) {
			return (SelectItem) valueItem;
		}
		String var = getVar("item");
		return FacesUtils.doWithRequestScopeVariable(getFacesContext(), var, valueItem, new Callable<SelectItem>() {
			public SelectItem call() throws Exception {
				Object value = getItemValue(valueItem);
				String label = getValueLabel(value);
				String description = getItemDescription();
				boolean disabled = isItemDisabled();
				boolean escape = isItemEscaped();
				return new SelectItem(valueItem, label, description, disabled, escape);
			}
		});
	}

	private String getVar(String defaultValue) {
		String var = getVar();
		return (var != null ? var : defaultValue);
	}

	private Object getItemValue(Object defaultValue) {
		return getStateHelper().eval(PropertyKeys.itemValue, defaultValue);
	}

	private String getValueLabel(Object value) {
		String itemLabel = getItemLabel();
		if (itemLabel == null) {
			FacesContext context = getFacesContext();
			ApplicationContext applicationContext = getApplicationContext(context);
			ObjectMessageSource messageSource = ObjectMessageSourceUtils.getObjectMessageSource(getMessageSource(),
					applicationContext);
			try {
				Locale locale = FacesUtils.getLocale(context);
				itemLabel = messageSource.getMessage(value, null, locale);
			} catch (NoSuchObjectMessageException e) {
			}
		}
		if (itemLabel == null) {
			itemLabel = String.valueOf(value);
		}
		return itemLabel;
	}

	private ApplicationContext getApplicationContext(FacesContext context) {
		Assert.notNull(context, "Context must not be null");
		ExternalContext externalContext = context.getExternalContext();
		if (SpringFacesIntegration.isInstalled(externalContext)) {
			return SpringFacesIntegration.getCurrentInstance(externalContext).getApplicationContext();
		}
		return null;
	}

	protected String convertToString(FacesContext context, UIComponent component, final Object value) {
		String var = getVar("item");
		// FIXME shortcut if not got expression
		return FacesUtils.doWithRequestScopeVariable(getFacesContext(), var, value, new Callable<String>() {
			public String call() throws Exception {
				String itemConvertedValue = getItemConvertedValue();
				// FIXME get value from @ID
				return itemConvertedValue == null ? value.toString() : itemConvertedValue;
			}
		});
	}

	@Override
	public void setParent(UIComponent parent) {
		detatch(getParent());
		super.setParent(parent);
		attatch(parent);
	}

	private void detatch(UIComponent parent) {
		if (parent != null) {
			parent.getChildren().remove(this.exposedUISelectItems);
			if (parent instanceof ValueHolder) {
				ValueHolder valueHolder = (ValueHolder) parent;
				if (valueHolder.getConverter() == this.converter) {
					valueHolder.setConverter(null);
				}
			}
		}
	}

	private void attatch(UIComponent parent) {
		if (parent != null) {
			parent.getChildren().add(this.exposedUISelectItems);
			if (parent instanceof ValueHolder) {
				ValueHolder valueHolder = (ValueHolder) parent;
				if (valueHolder.getConverter() == null) {
					valueHolder.setConverter(this.converter);
				}
			}
		}
	}

	public Object getValue() {
		return getStateHelper().eval(PropertyKeys.value);
	}

	public void setValue(Object value) {
		getStateHelper().put(PropertyKeys.value, value);
	}

	public String getVar() {
		return (String) getStateHelper().get(PropertyKeys.var);
	}

	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
	}

	public Object getItemValue() {
		return getStateHelper().eval(PropertyKeys.itemValue);
	}

	public void setItemValue(Object itemValue) {
		getStateHelper().put(PropertyKeys.itemValue, itemValue);
	}

	public String getItemLabel() {
		return (String) getStateHelper().eval(PropertyKeys.itemLabel);
	}

	public void setItemLabel(String itemLabel) {
		getStateHelper().put(PropertyKeys.itemLabel, itemLabel);
	}

	public String getItemDescription() {
		return (String) getStateHelper().eval(PropertyKeys.itemDescription);
	}

	public void setItemDescription(String itemDescription) {
		getStateHelper().put(PropertyKeys.itemDescription, itemDescription);
	}

	public boolean isItemDisabled() {
		return (Boolean) getStateHelper().eval(PropertyKeys.itemDisabled, false);
	}

	public void setItemDisabled(boolean itemDisabled) {
		getStateHelper().put(PropertyKeys.itemDisabled, itemDisabled);
	}

	public boolean isItemEscaped() {
		return (Boolean) getStateHelper().eval(PropertyKeys.itemEscaped, true);
	}

	public void setItemEscaped(boolean itemEscaped) {
		getStateHelper().put(PropertyKeys.itemEscaped, itemEscaped);
	}

	public String getItemConvertedValue() {
		return (String) getStateHelper().eval(PropertyKeys.itemConvertedValue);
	}

	public void setItemConvertedValue(String value) {
		getStateHelper().put(PropertyKeys.itemConvertedValue, value);
	}

	public MessageSource getMessageSource() {
		return (MessageSource) getStateHelper().eval(PropertyKeys.messageSource);
	}

	public void setMessageSource(MessageSource messageSource) {
		getStateHelper().put(PropertyKeys.messageSource, messageSource);
	}

	private enum PropertyKeys {
		value, var, itemValue, itemLabel, itemDescription, itemDisabled, itemEscaped, itemConvertedValue, messageSource
	}

	private class ExposedUISelectItems extends javax.faces.component.UISelectItems {
		@Override
		public String getId() {
			return UISelectItems.this.getId() + "_ExposedSelectItems";
		}

		@Override
		public Object getValue() {
			return UISelectItems.this.getSelectItems();
		}
	}

	private class UISelectItemsConverter extends SelectItemsConverter {

		public String getAsString(FacesContext context, UIComponent component, Object value) {
			return UISelectItems.this.convertToString(context, component, value);
		}
	}
}
