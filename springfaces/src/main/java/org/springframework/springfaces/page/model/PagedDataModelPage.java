package org.springframework.springfaces.page.model;

/**
 * Interface that represents a page of row data.
 * 
 * @param <T>
 * @see PagedDataModelPageProvider
 * 
 * @author Phillip Webb
 */
public interface PagedDataModelPage<T> {

	/**
	 * Returns the total number of rows contained in the underlying dataset. NOTE: this value is the <b>TOTAL</b> number
	 * of rows, not the number of rows represented by this page.
	 * @return the total row count
	 */
	long getTotalRowCount();

	/**
	 * Determines if the page contains data for the specified row index.
	 * @param rowIndex the row index. NOTE: this is the index as applied to the complete data set, not the page
	 * @return <tt>true</tt> if this page contains data at the specified row index
	 */
	boolean containsRowIndex(int rowIndex);

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
