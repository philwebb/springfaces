package org.springframework.springfaces.model;

import javax.faces.model.DataModel;

/**
 * A set of {@link DataModel} rows. This interface is used to provide access to a subset of data from any underlying
 * source.
 * 
 * @param <E> The element type
 * 
 * @author Phillip Webb
 */
public interface DataModelRowSet<E> {

	public static final long UNKNOWN_TOTAL_ROW_COUNT = -1;

	/**
	 * Returns the total number of rows contained in the underlying source or <tt>UNKNOWN_TOTAL_ROW_COUNT</tt> if the
	 * total row count is unknown.
	 * @return the total row count or <tt>UNKNOWN_TOTAL_ROW_COUNT</tt>
	 */
	long getTotalRowCount();

	/**
	 * Determines if this set contains the specified row index. NOTE: A set can contain data that might not be
	 * necessarily be {@link #isRowAvailable available}.
	 * @param rowIndex the row index. NOTE: this is the index as applied to the complete data model, not just this set
	 * @return <tt>true</tt> if this set contains data at the specified row index
	 * @see #isRowAvailable(int)
	 */
	boolean contains(int rowIndex);

	/**
	 * Determines if {@link #getRowData row data} is available from this set at the specified row index.
	 * @param rowIndex the row index. NOTE: this is the index as applied to the complete data model, not just this set
	 * @return <tt>true</tt> if row data is available
	 */
	boolean isRowAvailable(int rowIndex);

	/**
	 * Returns the row data at the specified index.
	 * @param rowIndex the row index. NOTE: this is the index as applied to the complete data set, not the page
	 * @return the row data
	 * @throws NoRowAvailableException if the page does {@link #contains contain} the specified row
	 */
	E getRowData(int rowIndex) throws NoRowAvailableException;
}
