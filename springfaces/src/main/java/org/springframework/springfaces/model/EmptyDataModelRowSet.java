package org.springframework.springfaces.model;


/**
 * Provides an empty {@link DataModelRowSet} implementation for a single row index.
 * 
 * @param <E> The element type
 * @author Phillip Webb
 */
public class EmptyDataModelRowSet<E> implements DataModelRowSet<E> {

	private int rowIndex;

	public EmptyDataModelRowSet(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public long getTotalRowCount() {
		return -1;
	}

	public boolean isRowAvailable(int rowIndex) {
		return false;
	}

	public E getRowData(int rowIndex) {
		throw new NoRowAvailableException();
	}

	public boolean contains(int rowIndex) {
		return this.rowIndex == rowIndex;
	}

	public static <E> EmptyDataModelRowSet<E> forRow(int rowIndex) {
		return new EmptyDataModelRowSet<E>(rowIndex);
	}
}
