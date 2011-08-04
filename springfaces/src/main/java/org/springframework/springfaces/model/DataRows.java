package org.springframework.springfaces.model;

import java.util.Iterator;

import javax.faces.model.DataModel;

/**
 * Provides per-row access to an underlying data source in an identical way to a JSF {@link DataModel}.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public interface DataRows<E> extends Iterable<E> {

	/**
	 * @see DataModel#isRowAvailable()
	 */
	boolean isRowAvailable();

	/**
	 * @see DataModel#getRowCount()
	 */
	int getRowCount();

	/**
	 * @see DataModel#isRowData()
	 */
	E getRowData();

	/**
	 * @see DataModel#getRowIndex()
	 */
	int getRowIndex();

	/**
	 * @see DataModel#setRowIndex
	 */
	void setRowIndex(int rowIndex);

	/**
	 * @see DataModel#iterator()
	 */
	Iterator<E> iterator();

}
