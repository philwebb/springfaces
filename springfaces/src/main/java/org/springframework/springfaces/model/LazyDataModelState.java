package org.springframework.springfaces.model;

import java.io.Serializable;

/**
 * State associated with a {@link LazyDataModel}.
 * 
 * @author Phillip Webb
 */
public class LazyDataModelState implements Serializable {

	private static final long serialVersionUID = 1L;

	private int rowIndex = -1;

	/**
	 * Returns the row index.
	 * @return the row index
	 * @see DataRows#getRowIndex()
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * Set the row index.
	 * @param rowIndex the row index
	 * @see DataRows#setRowIndex(int)
	 */
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
}
