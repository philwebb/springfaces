package org.springframework.springfaces.traveladvisor.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

//FIXME push up
//FIXME test
@Component
public class SelectItemsConverter implements Converter {

	// FIXME check with SelectItemsIterator. Implementation looks buggy

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
	// itemLabel - As standard, optional default JSF conversion of value
	// itemLableEscaped - Optional
	// itemValue - Optional default #{var}
	// itemDescription - Optional default null
	// itemDisabled - Optional default false
	// itemConvertedValue - The converted to string value, optional defaults to either @Id (if one) or value.toString()
	// includeNoSelection - Includes a noSelection option, defaults to false

	// FIXME check not more than one item with same value
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<SelectItem> selectItems = getSelectItems(context, component);
		for (SelectItem selectItem : selectItems) {
			String stringValue = getAsString(context, component, selectItem.getValue());
			if (ObjectUtils.nullSafeEquals(value, stringValue)) {
				return selectItem.getValue();
			}
		}
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value == null ? "" : value.toString();
	}

	protected List<SelectItem> getSelectItems(FacesContext context, UIComponent component) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		for (UIComponent child : component.getChildren()) {
			if (child instanceof UISelectItem) {
				addSelectItems(selectItems, (UISelectItem) child);
			} else if (child instanceof UISelectItems) {
				addSelectItems(context, selectItems, (UISelectItems) child);
			}
		}
		return selectItems;
	}

	private void addSelectItems(List<SelectItem> selectItems, UISelectItem selectItem) {
		selectItems.add(new SelectItem(selectItem.getItemValue(), selectItem.getItemLabel()));
	}

	@SuppressWarnings("unchecked")
	private void addSelectItems(FacesContext context, List<SelectItem> selectItems, UISelectItems uiSelectItems) {
		Object value = uiSelectItems.getValue();
		if (value instanceof SelectItem[]) {
			selectItems.addAll(Arrays.asList((SelectItem[]) value));
		} else if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				selectItems.add(new SelectItem(entry.getValue(), String.valueOf(entry.getKey())));
			}
		} else if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			String var = (String) uiSelectItems.getAttributes().get("var");
			if (var != null) {
				for (Object object : collection) {
					context.getExternalContext().getRequestMap().put(var, object);
					String itemLabel = (String) uiSelectItems.getAttributes().get("itemLabel");
					Object itemValue = uiSelectItems.getAttributes().get("itemValue");
					selectItems.add(new SelectItem(itemValue, itemLabel));
				}
			} else {
				selectItems.addAll((Collection<SelectItem>) collection);
			}
		}

	}
}
