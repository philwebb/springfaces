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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.springfaces.selectitems.SelectItems;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;

/**
 * Iterates {@link SelectItem}s from child {@link UISelectItem} or {@link UISelectItems} of the given component.
 * Intended for use with {@link UISelectOne} or {@link UISelectMany} components.
 * @see SelectItems
 * @author Phillip Webb
 */
public class SelectItemsIterator implements Iterator<SelectItem> {

	private static final Iterator<SelectItem> EMPTY = Collections.<SelectItem> emptySet().iterator();

	private Iterator<UIComponent> children;

	private Iterator<SelectItem> selectItems = EMPTY;

	private FacesContext context;

	public SelectItemsIterator(FacesContext context, UIComponent component) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(component, "Component must not be null");
		this.context = context;
		this.children = component.getChildren().iterator();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		if (this.selectItems.hasNext()) {
			return true;
		}
		while (this.children.hasNext()) {
			this.selectItems = newSelectItemsIterator(this.children.next());
			if (this.selectItems.hasNext()) {
				return true;
			}
		}
		return false;
	}

	public SelectItem next() {
		if (this.selectItems.hasNext()) {
			return this.selectItems.next();
		}
		while (this.children.hasNext()) {
			this.selectItems = newSelectItemsIterator(this.children.next());
			if (this.selectItems.hasNext()) {
				return this.selectItems.next();
			}
		}
		throw new NoSuchElementException();
	}

	private Iterator<SelectItem> newSelectItemsIterator(UIComponent component) {
		if (component instanceof UISelectItem) {
			return newSelectItemsIterator((UISelectItem) component);
		}
		if (component instanceof UISelectItems) {
			return newSelectItemsIterator((UISelectItems) component);
		}
		return EMPTY;
	}

	private Iterator<SelectItem> newSelectItemsIterator(UISelectItem component) {
		SelectItem value = (SelectItem) component.getValue();
		if (value == null) {
			value = new SelectItem(component.getItemValue(), component.getItemLabel(), component.getItemDescription(),
					component.isItemDisabled(), component.isItemEscaped(), component.isNoSelectionOption());
		}
		return Collections.singleton(value).iterator();
	}

	private Iterator<SelectItem> newSelectItemsIterator(final UISelectItems component) {
		final String var = (String) component.getAttributes().get("var");
		SelectItems selectItems = new SelectItems(component.getValue()) {
			@Override
			protected SelectItem convertToSelectItem(final Object value) {
				return FacesUtils.doWithRequestScopeVariable(SelectItemsIterator.this.context, var, value,
						new Callable<SelectItem>() {
							public SelectItem call() throws Exception {
								Map<String, Object> attrs = component.getAttributes();
								Object itemValue = firstNonNullValue(attrs.get("itemValue"), value);
								String itemLabel = getStringValue(firstNonNullValue(attrs.get("itemLabel"), itemValue));
								SelectItem item = new SelectItem(itemValue, itemLabel);
								item.setDescription(getStringValue(attrs.get("itemDescription")));
								item.setEscape(getBooleanValue(attrs.get("itemLabelEscaped")));
								item.setDisabled(getBooleanValue(attrs.get("itemDisabled")));
								item.setNoSelectionOption(getBooleanValue(firstNonNullValue(
										attrs.get("noSelectionOption"), attrs.get("noSelectionValue"))));
								return item;
							}
						});
			}
		};
		return selectItems.iterator();
	}
}
