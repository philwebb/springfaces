package org.springframework.springfaces.util;

import java.util.Arrays;
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

import org.springframework.util.Assert;

/**
 * Iterator for {@link SelectItem}s extracted from the children of a given component. Intended for use with
 * {@link UISelectOne} or {@link UISelectMany} components.
 * 
 * @author Phillip Webb
 */
public class SelectItemsIterator implements Iterator<SelectItem> {

	private Iterator<SelectItem> EMPTY_ITERATOR = Collections.<SelectItem> emptySet().iterator();

	private FacesContext context;

	private Iterator<UIComponent> components;

	private Iterator<SelectItem> currentSelectItems;

	public SelectItemsIterator(FacesContext context, UIComponent component) {
		Assert.notNull(component, "Component must not be null");
		components = component.getChildren().iterator();
	}

	protected final FacesContext getContext() {
		return context;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		if (currentSelectItems.hasNext()) {
			return true;
		}
		while (components.hasNext()) {
			currentSelectItems = newChildIterator(components.next());
			if (currentSelectItems.hasNext()) {
				return true;
			}
		}
		return false;
	}

	public SelectItem next() {
		if (currentSelectItems.hasNext()) {
			return currentSelectItems.next();
		}
		while (components.hasNext()) {
			currentSelectItems = newChildIterator(components.next());
			if (currentSelectItems.hasNext()) {
				return currentSelectItems.next();
			}
		}
		throw new NoSuchElementException();
	}

	private Iterator<SelectItem> newChildIterator(UIComponent component) {
		if (component instanceof UISelectItem) {
			return newChildIterator((UISelectItem) component);
		}
		if (component instanceof UISelectItems) {
			return newChildIterator((UISelectItems) component);
		}
		return EMPTY_ITERATOR;
	}

	private Iterator<SelectItem> newChildIterator(UISelectItem component) {
		SelectItem value = (SelectItem) component.getValue();
		if (value == null) {
			value = new SelectItem(component.getItemValue(), component.getItemLabel(), component.getItemDescription(),
					component.isItemDisabled(), component.isItemEscaped(), component.isNoSelectionOption());
		}
		return Collections.singleton((SelectItem) value).iterator();
	}

	private Iterator<SelectItem> newChildIterator(UISelectItems component) {
		Object value = component.getValue();
		return newChildIterator(component, value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Iterator<SelectItem> newChildIterator(UIComponent component, Object value) {
		if (value == null) {
			return EMPTY_ITERATOR;
		}
		if (value instanceof SelectItem) {
			Collections.singleton((SelectItem) value).iterator();
		}
		if (value.getClass().isArray()) {
			return new UISelectItemsIterator(component, Arrays.asList((Object[]) value).iterator());
		}
		if (value instanceof Iterable) {
			return new UISelectItemsIterator(component, ((Iterable) value).iterator());
		}
		if (value instanceof Map) {
			return new MapIterator(((Map) value).entrySet().iterator());
		}
		throw new IllegalArgumentException("Unsupport class type " + value.getClass().getName()
				+ " return from UISelectItems value");
	}

	protected final <T> T firstNonNullValue(T... values) {
		for (T value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	protected boolean getBooleanValue(Object o) {
		return getBooleanValue(o, Boolean.FALSE);
	}

	protected Boolean getBooleanValue(Object o, Boolean defaultValue) {
		if (o == null) {
			return defaultValue;
		}
		return (o instanceof Boolean ? (Boolean) o : Boolean.valueOf(o.toString()));
	}

	protected String getStringValue(Object o) {
		return o == null ? null : o.toString();
	}

	protected abstract class AdaptingIterator<E> implements Iterator<SelectItem> {

		private Iterator<E> source;

		public AdaptingIterator(Iterator<E> source) {
			this.source = source;
		}

		public boolean hasNext() {
			return source.hasNext();
		}

		public SelectItem next() {
			return adapt(source.next());
		}

		protected abstract SelectItem adapt(E sourceValue);

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("rawtypes")
	protected class MapIterator extends AdaptingIterator<Map.Entry> {

		public MapIterator(Iterator<Map.Entry> source) {
			super(source);
		}

		@Override
		protected SelectItem adapt(Map.Entry sourceValue) {
			Object value = firstNonNullValue(sourceValue.getValue(), "");
			String label = getStringValue(firstNonNullValue(sourceValue.getKey(), sourceValue.getValue(), ""));
			return new SelectItem(value, label);
		}
	}

	protected class UISelectItemsIterator extends AdaptingIterator<Object> {

		private UIComponent component;
		private String var;

		public UISelectItemsIterator(UIComponent component, Iterator<Object> source) {
			super(source);
			this.component = component;
			this.var = (String) component.getAttributes().get("var");
		}

		@Override
		protected SelectItem adapt(final Object sourceValue) {
			if (sourceValue instanceof SelectItem) {
				return (SelectItem) sourceValue;
			}
			return FacesUtils.doWithRequestScopeVariable(context, var, sourceValue, new Callable<SelectItem>() {
				public SelectItem call() throws Exception {
					Map<String, Object> attrs = component.getAttributes();
					Object value = firstNonNullValue(attrs.get("itemValue"), sourceValue);
					String label = getStringValue(firstNonNullValue(attrs.get("itemLabel"), value));
					SelectItem item = new SelectItem(value, label);
					item.setDescription(getStringValue(attrs.get("itemDescription")));
					item.setEscape(getBooleanValue(attrs.get("itemLabelEscaped")));
					item.setDisabled(getBooleanValue(attrs.get("itemDisabled")));
					item.setNoSelectionOption(getBooleanValue(
							firstNonNullValue(attrs.get("noSelectionOption"), attrs.get("noSelectionValue")), null));
					return item;
				}
			});
		}
	}
}
