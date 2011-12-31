package org.springframework.springfaces.selectitems.ui;

import java.util.Collections;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

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
	// itemLabel - As standard, optional default JSF conversion of value
	// itemLableEscaped - Optional
	// itemValue - Optional default #{var}
	// itemDescription - Optional default null
	// itemDisabled - Optional default false
	// itemConvertedValue - The converted to string value, optional defaults to either @Id (if one) or value.toString()
	// includeNoSelection - Includes a noSelection option, defaults to false
	public static final String COMPONENT_FAMILY = "spring.faces.SelectItems";

	private ExposedUISelectItems exposedUISelectItems = new ExposedUISelectItems();

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public void setParent(UIComponent parent) {
		UIComponent existingParent = getParent();
		if (existingParent != null) {
			existingParent.getChildren().remove(this.exposedUISelectItems);
		}
		super.setParent(parent);
		if (parent != null) {
			parent.getChildren().add(this.exposedUISelectItems);
		}
	}

	private class ExposedUISelectItems extends javax.faces.component.UISelectItems {
		@Override
		public String getId() {
			return UISelectItems.this.getId() + "_ExposedSelectItems";
		}

		@Override
		public Object getValue() {
			return Collections.singletonMap("key", "value");
		}
	}
}
