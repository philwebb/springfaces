package org.springframework.springfaces.page.model;

import org.springframework.springfaces.model.NoRowAvailableException;

/**
 * Provides an empty {@link PagedDataModelContent} implementation.
 * 
 * @param <T> The data type
 * @author Phillip Webb
 */
public class EmptyPagedDataModelPage<T> implements PagedDataModelContent<T> {

	private int rowIndex;

	public EmptyPagedDataModelPage(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public long getTotalRowCount() {
		return -1;
	}

	public boolean isRowAvailable(int rowIndex) {
		return false;
	}

	public T getRowData(int rowIndex) {
		throw new NoRowAvailableException();
	}

	public boolean contains(int rowIndex) {
		return this.rowIndex == rowIndex;
	}
}
