package org.springframework.springfaces.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.springframework.util.Assert;

/**
 * Base for {@link Iterator}s that selectively {@link #isElementFiltered(Object) filter} items from an underlying
 * source.
 * 
 * @param <E> the element type
 * @author Phillip Webb
 */
public abstract class FilteredIterator<E> implements Iterator<E> {

	private Iterator<E> sourceIterator;

	private E next;

	/**
	 * Create a new {@link FilteredIterator} instance.
	 * @param sourceIterator the source iterator.
	 */
	public FilteredIterator(Iterator<E> sourceIterator) {
		Assert.notNull(sourceIterator, "SourceIterator must not be null");
		this.sourceIterator = sourceIterator;
	}

	public boolean hasNext() {
		ensureNextHasBeenFetched();
		return next != null;
	}

	public E next() {
		try {
			ensureNextHasBeenFetched();
			if (next == null) {
				throw new NoSuchElementException();
			}
			return next;
		} finally {
			next = null;
		}
	}

	public void remove() {
		sourceIterator.remove();
	}

	private void ensureNextHasBeenFetched() {
		while (next == null && sourceIterator.hasNext()) {
			E candidate = sourceIterator.next();
			if (!isElementFiltered(candidate)) {
				next = candidate;
			}
		}
	}

	/**
	 * Determines if the element should be filtered.
	 * @param element the element
	 * @return <tt>true</tt> if the element is filtered
	 */
	protected abstract boolean isElementFiltered(E element);
}
