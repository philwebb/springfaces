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
package org.springframework.springfaces.selectitems.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.DataModel;
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
import org.springframework.util.ObjectUtils;

/**
 * Alternative to the standard JSF {@link javax.faces.component.UISelectItems} component that may be nested inside a
 * {@link UISelectMany} or {@link UISelectOne} component in order to add {@link SelectItem}s. The {@link #getValue()
 * value} attribute will be used to build the list of select items and may be bound to any object of the following type:
 * <ul>
 * <li>A {@link Collection}</li>
 * <li>An {@link Object} Array</li>
 * <li>A {@link String} containing a comma separated list of values</li>
 * <li>A {@link DataModel}</li>
 * </ul>
 * In addition it is possible to omit the {@link #getValue() value} attribute entirely when the parent component is
 * bound to a value of the following type:
 * <ul>
 * <li>A {@link Boolean} (Presents the values <tt>Yes</tt> and <tt>No</tt>)</li>
 * <li>An {@link Enum} (Presents the enum values)</li>
 * <li>Any generic typed {@link Collection} or Array of the above</li>
 * </ul>
 * <p>
 * Contents of {@link SelectItem} will be constructed using the optional {@link #getItemLabel() itemLabel},
 * {@link #isItemLabelEscaped() itemLabelEscaped}, {@link #getItemDescription() itemDescription},
 * {@link #isItemDisabled() itemDisabled} and {@link #getNoSelectionValue() noSelectionValue} attributes. Each of these
 * may make reference to the item value using via a EL variable (the name of the variable defaults to <tt>item</tt> but
 * can be changed using the {@link #getVar() var} attribute).
 * <p>
 * For example:
 * 
 * <pre>
 * &lt;s:selectItems value="#{customers}" itemLabel="#{item.name}"/&gt;
 * </pre>
 * 
 * <p>
 * If not explicitly specified the {@link #getItemLabel() itemLabel} will be deduced. If the Spring
 * {@link ApplicationContext} is linked to an {@link ObjectMessageSource} then this will be used to construct the label,
 * otherwise the <tt>toString()</tt> value will be used ({@link NoSuchObjectMessageException}s will be silently
 * ignored).
 * <p>
 * If the parent component does not have a JSF {@link Converter} defined then a {@link SelectItemsConverter} will be
 * automatically attached. The {@link #getItemConverterStringValue() itemConverterStringValue} attribute will be used as
 * the {@link Converter#getAsString(FacesContext, UIComponent, Object) getAsString} implementation. If the
 * {@link #getItemConverterStringValue() itemConverterStringValue} attribute is not specified a string will be created
 * either from using the item <tt>toString()</tt> method or ,if the object is a JPA <tt>@Entity</tt>, using the
 * <tt>@ID</tt> annotated field.
 * <p>
 * <strong>NOTE:</strong> It is imperative that each item has a unique <tt>itemConverterStringValue</tt> value.
 * <p>
 * By default a {@link SelectItem#isNoSelectionOption() noSelectionOption} {@link SelectItem} will by added if the
 * parent is a {@link UISelectOne} component. The {@link #setIncludeNoSelectionOption(Boolean) includeNoSelectionOption}
 * attribute can be used to override this behavior.
 * 
 * @author Phillip Webbb
 * @author Pedro Casagrande de Campos
 * @see ObjectMessageSource
 * @see SelectItemsConverter
 */
public class UISelectItems extends UIComponentBase {

	/**
	 * The message code used to look up any {@link #getIncludeNoSelectionOption() included} noSelectionOption item. If
	 * no message is found the {@link #NO_SELECTION_OPTION_DEFAULT_MESSAGE} will be used.
	 * @see #NO_SELECTION_OPTION_DEFAULT_MESSAGE
	 */
	public static final String NO_SELECTION_OPTION_MESSAGE_CODE = "spring.faces.noselectionoption";

	/**
	 * The message used for any {@link #getIncludeNoSelectionOption() included} noSelectionOption item.
	 * @see #NO_SELECTION_OPTION_MESSAGE_CODE
	 */
	public static final String NO_SELECTION_OPTION_DEFAULT_MESSAGE = "--- Please Select ---";

	public static final String COMPONENT_FAMILY = "spring.faces.SelectItems";

	private static final String DEFAULT_VAR = "item";

	private static Map<Object, String> DEFAULT_OBJECT_STRINGS;
	static {
		Map<Object, String> map = new HashMap<Object, String>();
		map.put(Boolean.TRUE, "Yes");
		map.put(Boolean.FALSE, "No");
		DEFAULT_OBJECT_STRINGS = Collections.unmodifiableMap(map);
	}

	private static final Object[] BOOLEAN_VALUES = { true, false };

	private ExposedUISelectItems exposedUISelectItems = new ExposedUISelectItems();

	private UISelectItemsConverter converter = new UISelectItemsConverter();

	private List<SelectItem> selectItems;

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
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

	/**
	 * Returns the {@link List} of {@link SelectItem}s that should be exposed to the parent component.
	 * @return the list of select items.
	 */
	protected final List<SelectItem> getSelectItems() {
		if (this.selectItems == null) {
			FacesContext context = getFacesContext();
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			addNoSelectionOptionAsRequired(context, selectItems);
			Iterable<Object> valueItems = getOrDeduceValues();
			for (Object valueItem : valueItems) {
				SelectItem selectItem = convertToSelectItem(context, valueItem);
				selectItems.add(selectItem);
			}
			this.selectItems = selectItems;
		}
		return this.selectItems;

	}

	private void addNoSelectionOptionAsRequired(FacesContext context, List<SelectItem> selectItems) {
		Boolean includeNoSelectionOption = getIncludeNoSelectionOption();
		if (includeNoSelectionOption == null) {
			includeNoSelectionOption = (getParent() instanceof UISelectOne);
		}
		if (includeNoSelectionOption) {
			SelectItem item = createNoSelectionOption(context);
			Assert.state(item != null, "No select item created");
			selectItems.add(item);
		}
	}

	/**
	 * Create the {@link SelectItem} for any {@link #getIncludeNoSelectionOption() included} noSelectionOption item.
	 * @param context the faces context
	 * @return a new select item
	 */
	protected SelectItem createNoSelectionOption(FacesContext context) {
		ObjectMessageSource objectMessageSource = getObjectMessageSource(context);
		Locale locale = FacesUtils.getLocale(context);
		String label = objectMessageSource.getMessage(NO_SELECTION_OPTION_MESSAGE_CODE, null,
				NO_SELECTION_OPTION_DEFAULT_MESSAGE, locale);
		return new SelectItem(null, label, null, false, true, true);
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> getOrDeduceValues() {
		Object values = getValue();
		if (values == null) {
			values = deduceValuesFromParentComponent();
		}
		if (values instanceof String) {
			String[] stringValues = ((String) values).split(",");
			for (int i = 0; i < stringValues.length; i++) {
				stringValues[i] = stringValues[i].trim();
			}
			values = stringValues;
		}
		if (values instanceof Object[]) {
			values = Arrays.asList((Object[]) values);
		}
		Assert.state(values instanceof Iterable, "The value type " + values.getClass()
				+ " is not supported, please use a Collection, Array or String");
		return (Iterable<Object>) values;
	}

	private Object deduceValuesFromParentComponent() {
		ValueExpression valueExpression = getParent().getValueExpression("value");
		Assert.notNull(valueExpression,
				"The 'value' attribute is requred as the parent component does not have a bound 'value'");
		TypeDescriptor type = getTypeDescriptor(valueExpression, getFacesContext().getELContext());
		Object valueForType = deduceValuesForType(type);
		Assert.notNull(valueForType,
				"The 'value' attribute is requred as select items cannot be deduced from parent componenet 'value' expression '"
						+ valueExpression + "'");
		return valueForType;
	}

	/**
	 * Returns a {@link TypeDescriptor} for the given {@link ValueExpression}.
	 * @param valueExpression The value expression
	 * @param elContext the EL Context
	 * @return the {@link TypeDescriptor} of the value expression
	 */
	protected TypeDescriptor getTypeDescriptor(ValueExpression valueExpression, ELContext elContext) {
		return ELUtils.getTypeDescriptor(valueExpression, elContext);
	}

	/**
	 * Deduces the values that should be used for the given type descriptor. By default this method will support
	 * {@link Boolean} and {@link Enum} types. Subclasses can implement additional support if required.
	 * @param type The type to deduce values for
	 * @return The values for the given type ({@link Collection}, <tt>Array</tt> or Comma Separated <tt>String</tt>
	 * return types are supported)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object deduceValuesForType(TypeDescriptor type) {
		if (type.isArray() || type.isCollection()) {
			type = type.getElementTypeDescriptor();
		}
		Class<?> classType = type.getType();
		if (Boolean.class.equals(classType) || Boolean.TYPE.equals(classType)) {
			return BOOLEAN_VALUES;
		}
		if (Enum.class.isAssignableFrom(classType)) {
			return EnumSet.allOf((Class<? extends Enum>) classType);
		}
		return null;
	}

	private SelectItem convertToSelectItem(final FacesContext context, final Object valueItem) {
		if (valueItem instanceof SelectItem) {
			return (SelectItem) valueItem;
		}
		final String var = getVar(DEFAULT_VAR);
		return FacesUtils.doWithRequestScopeVariable(context, var, valueItem, new Callable<SelectItem>() {
			public SelectItem call() throws Exception {
				Object value = getItemValue();
				if (value == null) {
					value = valueItem;
				}
				String label = getItemLabel(context, valueItem);
				String description = getItemDescription();
				boolean disabled = isItemDisabled();
				boolean escape = isItemLabelEscaped();
				Object noSelectionValue = getNoSelectionValue();
				boolean noSelectionOption = noSelectionValue != null
						&& ObjectUtils.nullSafeEquals(valueItem, noSelectionValue);
				return new SelectItem(value, label, description, disabled, escape, noSelectionOption);
			}
		});
	}

	private String getVar(String defaultValue) {
		String var = getVar();
		return (var != null ? var : defaultValue);
	}

	private String getItemLabel(FacesContext context, Object value) {
		String itemLabel = getItemLabel();
		if (itemLabel == null) {
			ObjectMessageSource messageSource = getObjectMessageSource(context);
			Locale locale = FacesUtils.getLocale(context);
			try {
				itemLabel = messageSource.getMessage(value, null, locale);
			} catch (NoSuchObjectMessageException e) {
			}
		}
		if (itemLabel == null) {
			itemLabel = deduceItemLabel(value);
		}
		Assert.notNull(itemLabel, "Unable to deduce item label");
		return itemLabel;
	}

	private ObjectMessageSource getObjectMessageSource(FacesContext context) {
		ApplicationContext applicationContext = getApplicationContext(context);
		ObjectMessageSource messageSource = ObjectMessageSourceUtils.getObjectMessageSource(getMessageSource(),
				applicationContext);
		return messageSource;
	}

	/**
	 * Deduce the item label for the given object. This method is called when no {@link ObjectMessageSource} mapping is
	 * found. By default this method will use <tt>value.toString()</tt>, except with {@link Boolean} values where the
	 * strings "Yes" and "No" will be used.
	 * @param value the value to deduce
	 * @return the label of the item
	 */
	protected String deduceItemLabel(Object value) {
		if (DEFAULT_OBJECT_STRINGS.containsKey(value)) {
			return DEFAULT_OBJECT_STRINGS.get(value);
		}
		return String.valueOf(value);
	}

	private ApplicationContext getApplicationContext(FacesContext context) {
		Assert.notNull(context, "Context must not be null");
		ExternalContext externalContext = context.getExternalContext();
		if (SpringFacesIntegration.isInstalled(externalContext)) {
			return SpringFacesIntegration.getCurrentInstance(externalContext).getApplicationContext();
		}
		return null;
	}

	/**
	 * Returns the item value converted to a String. This method is called by the
	 * {@link SelectItemsConverter#getAsString(FacesContext, UIComponent, Object) select items converter}. By default
	 * this method will attempt to use the {@link #getItemConverterStringValue() itemConverterStringValue} attribute,
	 * falling back to {@link #deduceItemConverterStringValue(Object)}.
	 * @param value the value to convert
	 * @return the converted <tt>String</tt> value
	 * @see #deduceItemConverterStringValue(Object)
	 */
	protected final String getItemConverterStringValue(final Object value) {
		String var = getVar(DEFAULT_VAR);
		return FacesUtils.doWithRequestScopeVariable(getFacesContext(), var, value, new Callable<String>() {
			public String call() throws Exception {
				String itemConverterStringValue = getItemConverterStringValue();
				return itemConverterStringValue == null ? deduceItemConverterStringValue(value)
						: itemConverterStringValue;
			}
		});
	}

	/**
	 * Deduce the item value converted to a <tt>String</tt>. By default this method will use the <tt>@Id</tt> field of
	 * any <tt>@Entity</tt>, falling back to <tt>value.toString()</tt>.
	 * @param value the value to convert
	 * @return the converted <tt>String</tt> value
	 * @see #getItemConverterStringValue(Object)
	 */
	protected String deduceItemConverterStringValue(final Object value) {
		if (value != null) {
			Object entityId = SelectItemsJpaSupport.getInstance().getEntityId(value);
			if (entityId != null) {
				return entityId.toString();
			}
		}
		return (value == null ? "" : value.toString());
	}

	/**
	 * Return the request-scope attribute under which the current <tt>value</tt> will be exposed. This variable can be
	 * referenced from the {@link #getItemLabel() itemLabel}, {@link #isItemLabelEscaped() itemLabelEscaped},
	 * {@link #getItemDescription() itemDescription}, {@link #isItemDisabled() itemDisabled},
	 * {@link #getNoSelectionValue() noSelectionValue} and {@link #getItemConverterStringValue()
	 * itemConverterStringValue} attributes. If not specified the <tt>var</tt> "item" will be used.This property is
	 * <b>not</b> enabled for value binding expressions.
	 * @return The variable name
	 * @see #getValue()
	 */
	public String getVar() {
		return (String) getStateHelper().get(PropertyKeys.var);
	}

	/**
	 * Set the request-scope attribute under which the current <tt>value</tt> will be exposed.
	 * @param var The new request-scope attribute name
	 * @see #getVar()
	 */
	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
	}

	/**
	 * Returns the value that should be made available as {@link SelectItem}s. Value can refer to a {@link Collection} ,
	 * <tt>Array</tt> or a <tt>String</tt> containing comma separated values. If not specified the value will be deduced
	 * from the parent component value binding. Items are converted to select items used the {@link #getItemLabel()
	 * itemLabel}, {@link #isItemLabelEscaped() itemLabelEscaped}, {@link #getItemDescription() itemDescription},
	 * {@link #isItemDisabled() itemDisabled}, {@link #getNoSelectionValue() noSelectionValue} and
	 * {@link #getItemConverterStringValue() itemConverterStringValue} attributes.
	 * @return the value to expose as select items
	 * @see #getVar()
	 */
	public Object getValue() {
		return getStateHelper().eval(PropertyKeys.value);
	}

	/**
	 * Set the value that should be made available as {@link SelectItem}s.
	 * @param value the value
	 * @see #getValue()
	 */
	public void setValue(Object value) {
		getStateHelper().put(PropertyKeys.value, value);
	}

	/**
	 * Returns the {@link SelectItem#getValue() value} that should be used for the select item. This expression can
	 * refer to the current value using the {@link #getVar() var} attribute.
	 * @return the item label
	 */
	public Object getItemValue() {
		return getStateHelper().eval(PropertyKeys.itemValue);
	}

	/**
	 * Sets the item value.
	 * @param itemValue the item value
	 * @see #getItemValue()
	 */
	public void setItemValue(Object itemValue) {
		getStateHelper().put(PropertyKeys.itemValue, itemValue);
	}

	/**
	 * Returns the {@link SelectItem#getLabel() label} that should be used for the select item. This expression can
	 * refer to the current value using the {@link #getVar() var} attribute.
	 * @return the item label
	 */
	public String getItemLabel() {
		return (String) getStateHelper().eval(PropertyKeys.itemLabel);
	}

	/**
	 * Set the item label
	 * @param itemLabel the item label
	 * @see #getItemLabel()
	 */
	public void setItemLabel(String itemLabel) {
		getStateHelper().put(PropertyKeys.itemLabel, itemLabel);
	}

	/**
	 * Returns the {@link SelectItem#getDescription() description} that should be used for the select item. This
	 * expression can refer to the current value using the {@link #getVar() var} attribute.
	 * @return the item description
	 */
	public String getItemDescription() {
		return (String) getStateHelper().eval(PropertyKeys.itemDescription);
	}

	/**
	 * Set the item description
	 * @param itemDescription the item description
	 * @see #getItemDescription()
	 */
	public void setItemDescription(String itemDescription) {
		getStateHelper().put(PropertyKeys.itemDescription, itemDescription);
	}

	/**
	 * Returns if the select item is {@link SelectItem#isDisabled() disabled}. This expression can refer to the current
	 * value using the {@link #getVar() var} attribute.
	 * @return if the item is disabled
	 */
	public boolean isItemDisabled() {
		return (Boolean) getStateHelper().eval(PropertyKeys.itemDisabled, false);
	}

	/**
	 * Set if the item is disabled
	 * @param itemDisabled if the item is disabled
	 * @see #isItemDisabled()
	 */
	public void setItemDisabled(boolean itemDisabled) {
		getStateHelper().put(PropertyKeys.itemDisabled, itemDisabled);
	}

	/**
	 * Returns if the select item label is {@link SelectItem#isEscape() escaped}. This expression can refer to the
	 * current value using the {@link #getVar() var} attribute.
	 * @return if the item is escaped
	 */
	public boolean isItemLabelEscaped() {
		return (Boolean) getStateHelper().eval(PropertyKeys.itemLabelEscaped, true);
	}

	/**
	 * Set if the item is escaped
	 * @param itemLabelEscaped if the item label is escaped
	 * @see #isItemLabelEscaped()
	 */
	public void setItemLabelEscaped(boolean itemLabelEscaped) {
		getStateHelper().put(PropertyKeys.itemLabelEscaped, itemLabelEscaped);
	}

	/**
	 * Returns the converter string value that should be used for the select item. This expression can refer to the
	 * current value using the {@link #getVar() var} attribute. NOTE: in this context var is
	 * {@link SelectItem#getValue()}, this may differ from the item value if a custom {@link #getItemValue itemValue}
	 * has been specified.
	 * @return the converter string value
	 */
	public String getItemConverterStringValue() {
		return (String) getStateHelper().eval(PropertyKeys.itemConverterStringValue);
	}

	/**
	 * Set the converter string value.
	 * @param converterStringValue the converter string value
	 * @see #getItemConverterStringValue()
	 */
	public void setItemConverterStringValue(String converterStringValue) {
		getStateHelper().put(PropertyKeys.itemConverterStringValue, converterStringValue);
	}

	/**
	 * Returns the value for the {@link SelectItem#isNoSelectionOption() no selection option}. This expression can refer
	 * to the current value using the {@link #getVar() var} attribute.
	 * @return if the no selection value
	 */
	public Object getNoSelectionValue() {
		return getStateHelper().eval(PropertyKeys.noSelectionValue);
	}

	/**
	 * Set if the no selection value.
	 * @param noSelectionValue the no selection value
	 * @see #getNoSelectionValue()
	 */
	public void setNoSelectionValue(Object noSelectionValue) {
		getStateHelper().put(PropertyKeys.noSelectionValue, noSelectionValue);
	}

	/**
	 * Returns if a {@link SelectItem#isNoSelectionOption() noSelectionOption} item is included. If this value is not
	 * specified a no selection item will automatically be included for {@link UISelectOne} components only. The
	 * noSelectionItem will be inserted before other select items with a <tt>null</tt> value and a label
	 * {@value #NO_SELECTION_OPTION_MESSAGE_CODE looked up} from the {@link #getMessageSource() MessageSource}.
	 * @return if a noSelectionOption item is included
	 */
	public Boolean getIncludeNoSelectionOption() {
		return (Boolean) getStateHelper().eval(PropertyKeys.includeNoSelectionOption);
	}

	/**
	 * Sets if a {@link SelectItem#isNoSelectionOption() noSelectionOption} item is included.
	 * @param includeNoSelectionOption if a noSelectionOption item is included
	 * @see #getIncludeNoSelectionOption()
	 */
	public void setIncludeNoSelectionOption(Boolean includeNoSelectionOption) {
		getStateHelper().put(PropertyKeys.includeNoSelectionOption, includeNoSelectionOption);
	}

	/**
	 * Return the {@link MessageSource} or {@link ObjectMessageSource} that should be used construct the item label
	 * (when the {@link #getItemLabel() itemLabel} attribute is not specified). If not specified the
	 * {@link ApplicationContext} will be used.
	 * @return the message source
	 */
	public MessageSource getMessageSource() {
		return (MessageSource) getStateHelper().eval(PropertyKeys.messageSource);
	}

	/**
	 * Set the message source.
	 * @param messageSource the message source
	 * @see #getMessageSource()
	 */
	public void setMessageSource(MessageSource messageSource) {
		getStateHelper().put(PropertyKeys.messageSource, messageSource);
	}

	private enum PropertyKeys {
		value, var, itemValue, itemLabel, itemDescription, itemDisabled, itemLabelEscaped, itemConverterStringValue, noSelectionValue, includeNoSelectionOption, messageSource
	}

	/**
	 * Internal JSF {@link javax.faces.component.UISelectItems} used to expose items from the outer class.
	 */
	class ExposedUISelectItems extends javax.faces.component.UISelectItems {
		@Override
		public String getId() {
			return UISelectItems.this.getId() + "_ExposedSelectItems";
		}

		@Override
		public Object getValue() {
			return UISelectItems.this.getSelectItems();
		}
	}

	/**
	 * Internal JSF {@link Converter} used to convert items from the outer class.
	 */
	public static class UISelectItemsConverter extends SelectItemsConverter {

		public String getAsString(FacesContext context, UIComponent component, Object value) {
			return getUISelectItems(component).getItemConverterStringValue(value);
		}

		private UISelectItems getUISelectItems(UIComponent component) {
			for (UIComponent child : component.getChildren()) {
				if (child instanceof UISelectItems) {
					return (UISelectItems) child;
				}
			}
			throw new IllegalStateException("Unable to find UISelectItems in childen of " + component.getClientId());
		}
	}
}
