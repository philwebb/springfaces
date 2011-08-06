package org.springframework.springfaces.page.model;

import org.springframework.springfaces.model.NoRowAvailableException;

/**
 * Interface that represents contents for a {@link PagedDataModel}. Content is retrieved
 * 
 * @param <T> The data type
 * 
 * @See PagedDataModel
 * @see DataModelPageProvider
 * 
 * @author Phillip Webb
 */
public interface PagedDataModelContent<T> {

	/**
	 * Returns the total number of rows contained in the underlying dataset.
	 * @return the total row count
	 */
	long getTotalRowCount();

	/**
	 * Determines if this instance contains the specified row index. NOTE: Content can be contained that might not be
	 * {@link #isRowAvailable available}.
	 * @param rowIndex the row index. NOTE: this is the index as applied to the complete data set, not the page
	 * @return <tt>true</tt> if this page contains data at the specified row index
	 * @see #isRowAvailable(int)
	 */
	boolean contains(int rowIndex);

	// FIXME rename contains
	// FIXME DC
	boolean isRowAvailable(int rowIndex);

	/**
	 * Returns the row data at the specified index.
	 * @param rowIndex the row index. NOTE: this is the index as applied to the complete data set, not the page
	 * @return the row data
	 * @throws NoRowAvailableException if the page does {@link #containsRowIndex contain} the specified row
	 */
	T getRowData(int rowIndex) throws NoRowAvailableException;
}
