package org.springframework.springfaces.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.springframework.util.Assert;

/**
 * Iterates {@link SelectItem}s for a given value. Subclasses must provide a {@link #convertToSelectItem}
 * implementation.
 * <p>
 * The following value objects are supported:
 * <ul>
 * <li>A single {@link SelectItem}.</li>
 * <li>A {@link Map} of {@link Object}s. Each entry from the map is converted to a single {@link SelectItem} with the
 * <tt>value</tt> exposed as <tt>SelectItem.getValue()</tt> and the key used as <tt>SelectItem.getLabel()</tt>.</li>
 * <li>A {@link Collection}, {@link Iterable} or <tt>Array</tt> of objects. Any contained object that is not a
 * {@link SelectItem}s will be converted using the {@link #convertToSelectItem} method.</li>
 * </ul>
 * 
 * @author Phillip Webb
 */
public abstract class SelectItems implements Iterable<SelectItem> {

	private Object value;

	/**
	 * Create a new {@link SelectItems} instance.
	 * @param value the object used to extract the {@link SelectItem}s (can be null).
	 */
	public SelectItems(Object value) {
		this.value = value;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator<SelectItem> iterator() {
		if (value == null) {
			return Collections.<SelectItem> emptySet().iterator();
		}
		if (value instanceof SelectItem) {
			return Collections.singleton((SelectItem) value).iterator();
		}
		if (value instanceof Map) {
			return new MapEntryIterator(((Map) value).entrySet());
		}
		if (value.getClass().isArray()) {
			return new ValuesIterator(Arrays.asList((Object[]) value));
		}
		if (value instanceof Iterable) {
			return new ValuesIterator(((Iterable) value));
		}
		throw new IllegalArgumentException("Unsupport class type " + value.getClass().getName()
				+ " for SelectItem value");
	}

	/**
	 * Convert the specified value into a {@link SelectItem}. Subclasses can implement their own strategy for converting
	 * values (for example, by using EL expressions).
	 * @param value the value to convert (never be a collection type, {@link SelectItem} or <tt>null</tt>)
	 * @return a select item relevant to the value.
	 */
	protected abstract SelectItem convertToSelectItem(Object value);

	/**
	 * Utility method that will return the first <tt>non-null</tt> value from the given array.
	 * @param <T> the array type
	 * @param values an array of values
	 * @return the first <tt>non-null</tt> value from the array or <tt>null</tt> if the array is empty or contains only
	 * <tt>null</tt>s
	 */
	protected final <T> T firstNonNullValue(T... values) {
		for (T value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Utility method get a boolean value from an object. Returns <tt>false</tt> if the object is <tt>null</tt>.
	 * @param o the source object can be a Boolean or any object with a {@link Object#toString() toString()} that
	 * returns <tt>"true"</tt> or <tt>"false"</tt>.
	 * @return a boolean value.
	 * @see #getBooleanValue(Object, Boolean)
	 */
	protected boolean getBooleanValue(Object o) {
		return getBooleanValue(o, Boolean.FALSE);
	}

	/**
	 * Utility method get a boolean value from an object. Returns the specified default if the object is <tt>null</tt>.
	 * @param o the source object can be a Boolean or any object with a {@link Object#toString() toString()} that
	 * returns <tt>"true"</tt> or <tt>"false"</tt>.
	 * @param defaultValue The default value to return if the object is <tt>null</tt>
	 * @return a Boolean value.
	 * @see #getBooleanValue(Object)
	 */
	protected Boolean getBooleanValue(Object o, Boolean defaultValue) {
		if (o == null) {
			return defaultValue;
		}
		return (o instanceof Boolean ? (Boolean) o : Boolean.valueOf(o.toString()));
	}

	/**
	 * Utility method to convert an {@link Object} to a {@link String}.
	 * @param o The object
	 * @return a {@link String} value of the object or <tt>null</tt> if the object was null.
	 */
	protected String getStringValue(Object o) {
		return o == null ? null : o.toString();
	}

	/**
	 * Returns the value object acting as the source of the select items.
	 * @return the value
	 */
	protected final Object getValue() {
		return value;
	}

	/**
	 * Decorator class that adapts a source {@link Iterator} by {@link #adapt(Object) adapting} objects to
	 * {@link SelectItem}s.
	 * @param <E> The source iterator type.
	 */
	private abstract class AdaptingIterator<E> implements Iterator<SelectItem> {

		private Iterator<E> source;

		/**
		 * Create a new {@link AdaptingIterator} instance.
		 * @param source the source iterator
		 */
		public AdaptingIterator(Iterable<E> source) {
			Assert.notNull(source, "Source must not be null");
			this.source = source.iterator();
		}

		public boolean hasNext() {
			return source.hasNext();
		}

		public SelectItem next() {
			return adapt(source.next());
		}

		/**
		 * Adapt a value from the source iterator to a {@link SelectItem}.
		 * @param sourceValue the source value to adapt
		 * @return a {@link SelectItem}
		 */
		protected abstract SelectItem adapt(E sourceValue);

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Decorator class that adapts a source {@link Iterator} containing {@link Map.Entry map entries} to
	 * {@link SelectItem}s.
	 */
	@SuppressWarnings("rawtypes")
	private class MapEntryIterator extends AdaptingIterator<Map.Entry> {

		/**
		 * Create a new {@link MapEntryIterator} instance.
		 * @param source the source iterator
		 */
		public MapEntryIterator(Set<Map.Entry> source) {
			super(source);
		}

		@Override
		protected SelectItem adapt(Map.Entry sourceValue) {
			Object value = firstNonNullValue(sourceValue.getValue(), "");
			String label = getStringValue(firstNonNullValue(sourceValue.getKey(), sourceValue.getValue(), ""));
			return new SelectItem(value, label);
		}
	}

	/**
	 * Decorator class that adapts a source {@link Iterator} to {@link SelectItem}s by calling the
	 * {@link SelectItems#convertToSelectItem(Object)} method.
	 */
	private class ValuesIterator extends AdaptingIterator<Object> {

		/**
		 * Create a new {@link ValuesIterator} instance.
		 * @param source the source iterator
		 */
		public ValuesIterator(Iterable<Object> source) {
			super(source);
		}

		@Override
		protected SelectItem adapt(final Object sourceValue) {
			if (sourceValue instanceof SelectItem) {
				return (SelectItem) sourceValue;
			}
			SelectItem selectItem = convertToSelectItem(sourceValue);
			Assert.state(selectItem != null, "Unexpected null result when converting source value to a SelectItem");
			return selectItem;
		}
	}
}
