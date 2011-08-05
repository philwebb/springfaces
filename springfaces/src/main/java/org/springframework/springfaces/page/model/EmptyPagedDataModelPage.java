package org.springframework.springfaces.page.model;

/**
 * Provides access to an empty {@link PagedDataModelPage} implementation.
 * 
 * @author Phillip Webb
 */
class EmptyPagedDataModelPage<T> implements PagedDataModelPage<T> {

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

	public boolean containsRowIndex(int rowIndex) {
		return this.rowIndex == rowIndex;
	}
}
