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

/**
 * Alternative to the standard JSF {@link javax.faces.component.UISelectItems} component that may be nested inside a
 * {@link UISelectMany} or {@link UISelectOne} component in order to add {@link SelectItem}s. The {@link #getValues()
 * values} attribute will be used to build the list of select items and may be bound to any object of the following
 * type:
 * <ul>
 * <li>A {@link Collection}</li>
 * <li>An {@link Object} Array</li>
 * <li>A {@link String} containing a comma separated list of values</li>
 * </ul>
 * In addition it is possible to omit the {@link #getValues() values} attribute entirely when the parent component is
 * bound to a value of the following type:
 * <ul>
 * <li>A {@link Boolean} (Presents the values <tt>yes</tt> and <tt>no</tt>)</li>
 * <li>An {@link Enum} (Presents the enum values)</li>
 * <li>Any generic typed {@link Collection} or Array of the above</li>
 * </ul>
 * <p>
 * Contents of {@link SelectItem} will be constructed using the optional {@link #getItemLabel() itemLabel},
 * {@link #isItemEscape() itemEscape}, {@link #getItemDescription() itemDescription} and {@link #isItemDisabled()
 * itemDisabled} attributes. Each of these may make reference to the item value using via a EL variable (the name of the
 * variable defaults to <tt>item</tt> but can be changed using the {@link #getVar() var} attribute).
 * <p>
 * For example:
 * 
 * <pre>
 * &lt;s:selectItems values="#{customers}" itemLabel="#{item.name}"/&gt;
 * </pre>
 * 
 * <p>
 * If not explicitly specified the {@link #getItemLabel() itemLabel} will be deduced. If the Spring
 * {@link ApplicationContext} is linked to an {@link ObjectMessageSource} then this will be used to construct the label,
 * otherwise the <tt>toString()</tt> value will be used ({@link NoSuchObjectMessageException}s will be silently
 * ignored).
 * <p>
 * If the parent component does not have a JSF {@link Converter} defined then a {@link SelectItemsConverter} will be
 * automatically attached. The {@link #getItemConverterStringValue() itemConverterStringValue} attribute will be used to
 * as the {@link Converter#getAsString(FacesContext, UIComponent, Object) getAsString} implementation. If the
 * {@link #getItemConverterStringValue() itemConverterStringValue} attribute is not specified a string will be created
 * either from using the item <tt>toString()</tt> method or ,if the object is a JPA <tt>@Entity</tt>, using the
 * <tt>@ID</tt> annotated field.
 * <p>
 * <strong>NOTE:</strong> It is imperative that each item has a unique <tt>itemConverterStringValue</tt> value.
 * 
 * @see ObjectMessageSource
 * @see SelectItemsConverter
 * @author Phillip Webbb
 */
public class UISelectItems extends UIComponentBase {

	// FIXME Do we want a noSelectionValue? What if the converted item is a no selection?

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

	protected List<SelectItem> getSelectItems() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		Collection<Object> valueItems = getOrDeduceValues();
		for (Object valueItem : valueItems) {
			SelectItem selectItem = convertToSelectItem(valueItem);
			selectItems.add(selectItem);
		}
		return selectItems;
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> getOrDeduceValues() {
		Object values = getValues();
		if (values == null) {
			values = deduceValuesFromParentComponent();
		}
		if (values instanceof String) {
			values = ((String) values).split(",");
		}
		if (values instanceof Object[]) {
			values = Arrays.asList((Object[]) values);
		}
		Assert.state(values instanceof Collection, "The values type " + values.getClass()
				+ " is not supported, please use a Collection, Array or String");
		return (Collection<Object>) values;
	}

	private Object deduceValuesFromParentComponent() {
		ValueExpression valueExpression = getParent().getValueExpression("value");
		Assert.notNull(valueExpression,
				"The 'values' attribute is requred as the parent component does not have a bound 'values'");
		TypeDescriptor type = getTypeDescriptor(valueExpression, getFacesContext().getELContext());
		Object valueForType = deduceValuesForType(type);
		Assert.notNull(valueForType,
				"The 'values' attribute is requred as select items cannot be deduced from parent componenet 'values' expression '"
						+ valueExpression + "'");
		return valueForType;
	}

	protected TypeDescriptor getTypeDescriptor(ValueExpression valueExpression, ELContext elContext) {
		return ELUtils.getTypeDescriptor(valueExpression, elContext);
	}

	@SuppressWarnings("unchecked")
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

	private SelectItem convertToSelectItem(final Object valueItem) {
		if (valueItem instanceof SelectItem) {
			return (SelectItem) valueItem;
		}
		final FacesContext context = getFacesContext();
		final String var = getVar(DEFAULT_VAR);
		return FacesUtils.doWithRequestScopeVariable(context, var, valueItem, new Callable<SelectItem>() {
			public SelectItem call() throws Exception {
				String label = getItemLabel(valueItem);
				String description = getItemDescription();
				boolean disabled = isItemDisabled();
				boolean escape = isItemEscape();
				return new SelectItem(valueItem, label, description, disabled, escape);
			}
		});
	}

	private String getVar(String defaultValue) {
		String var = getVar();
		return (var != null ? var : defaultValue);
	}

	private String getItemLabel(Object value) {
		String itemLabel = getItemLabel();
		if (itemLabel == null) {
			FacesContext context = getFacesContext();
			ApplicationContext applicationContext = getApplicationContext(context);
			ObjectMessageSource messageSource = ObjectMessageSourceUtils.getObjectMessageSource(getMessageSource(),
					applicationContext);
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

	protected String deduceItemConverterStringValue(final Object value) {
		// FIXME JPA entity IDs
		return String.valueOf(value);
	}

	public Object getValues() {
		return getStateHelper().eval(PropertyKeys.values);
	}

	public void setValues(Object value) {
		getStateHelper().put(PropertyKeys.values, value);
	}

	public String getVar() {
		return (String) getStateHelper().get(PropertyKeys.var);
	}

	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
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

	public boolean isItemEscape() {
		return (Boolean) getStateHelper().eval(PropertyKeys.itemEscape, true);
	}

	public void setItemEscape(boolean itemEscape) {
		getStateHelper().put(PropertyKeys.itemEscape, itemEscape);
	}

	public String getItemConverterStringValue() {
		return (String) getStateHelper().eval(PropertyKeys.itemConverterStringValue);
	}

	public void setItemConverterStringValue(String value) {
		getStateHelper().put(PropertyKeys.itemConverterStringValue, value);
	}

	public MessageSource getMessageSource() {
		return (MessageSource) getStateHelper().eval(PropertyKeys.messageSource);
	}

	public void setMessageSource(MessageSource messageSource) {
		getStateHelper().put(PropertyKeys.messageSource, messageSource);
	}

	private enum PropertyKeys {
		values, var, itemLabel, itemDescription, itemDisabled, itemEscape, itemConverterStringValue, messageSource
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
	class UISelectItemsConverter extends SelectItemsConverter {
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			return UISelectItems.this.getItemConverterStringValue(value);
		}
	}
}
