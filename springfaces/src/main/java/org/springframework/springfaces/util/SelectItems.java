package org.springframework.springfaces.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

/**
 * Iterates {@link SelectItem}s for a given value. Subclasses must provide a {@link #createSelectItem(Object)}
 * implementation.
 * 
 * @author Phillip Webb
 */
public abstract class SelectItems implements Iterable<SelectItem> {

	private Object value;

	public SelectItems(Object value) {
		this.value = value;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator<SelectItem> iterator() {
		if (value == null) {
			return Collections.<SelectItem> emptySet().iterator();
		}
		if (value instanceof SelectItem) {
			Collections.singleton((SelectItem) value).iterator();
		}
		if (value instanceof Map) {
			return new MapIterator(((Map) value).entrySet());
		}
		if (value.getClass().isArray()) {
			return new ValuesIterator(Arrays.asList((Object[]) value));
		}
		if (value instanceof Iterable) {
			return new ValuesIterator(((Iterable) value));
		}
		throw new IllegalArgumentException("Unsupport class type " + value.getClass().getName()
				+ " return from UISelectItems value");
	}

	protected abstract SelectItem createSelectItem(Object value);

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

	protected final Object getValue() {
		return value;
	}

	protected abstract class AdaptingIterator<E> implements Iterator<SelectItem> {

		private Iterator<E> source;

		public AdaptingIterator(Iterable<E> source) {
			this.source = source.iterator();
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

		public MapIterator(Set<Map.Entry> source) {
			super(source);
		}

		@Override
		protected SelectItem adapt(Map.Entry sourceValue) {
			Object value = firstNonNullValue(sourceValue.getValue(), "");
			String label = getStringValue(firstNonNullValue(sourceValue.getKey(), sourceValue.getValue(), ""));
			return new SelectItem(value, label);
		}
	}

	protected class ValuesIterator extends AdaptingIterator<Object> {

		public ValuesIterator(Iterable<Object> source) {
			super(source);
		}

		@Override
		protected SelectItem adapt(final Object sourceValue) {
			if (sourceValue instanceof SelectItem) {
				return (SelectItem) sourceValue;
			}
			return createSelectItem(sourceValue);
		}
	}
}
