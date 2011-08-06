package org.springframework.springfaces.model;

/**
 * State associated with a {@link LazyDataModel}.
 * 
 * @author Phillip Webb
 */
public interface LazyDataModelState {

	/**
	 * Returns the row index.
	 * @return the row index
	 * @see DataRows#getRowIndex()
	 */
	int getRowIndex();

	/**
	 * Set the row index.
	 * @param rowIndex the row index
	 * @see DataRows#setRowIndex(int)
	 */
	void setRowIndex(int rowIndex);
}
