/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.model;

import java.util.Iterator;

import javax.faces.model.DataModel;

/**
 * Provides per-row access to an underlying data source in an identical way to a JSF {@link DataModel}.
 * @param <E>
 * @author Phillip Webb
 */
public interface DataRows<E> extends Iterable<E> {

	/**
	 * Returns <tt>true</tt> if the {@link #getRowData()} is available for the {@link #setRowIndex selected} row.
	 * @return <tt>true</tt> if row data is available.
	 * @see DataModel#isRowAvailable()
	 */
	boolean isRowAvailable();

	/**
	 * Returns the total number of rows available or <tt>-1</tt> if the number of rows is unknown.
	 * @return the row count
	 * @see DataModel#getRowCount()
	 */
	int getRowCount();

	/**
	 * Returns row data for the {@link #setRowIndex selected} row.
	 * @return the row data
	 * @see DataModel#getRowData()
	 */
	E getRowData();

	/**
	 * Returns the currently selected row index or <tt>-1</tt> if no row is currently selected.
	 * @return the row index
	 * @see DataModel#getRowIndex()
	 */
	int getRowIndex();

	/**
	 * Sets the currently selected row index.
	 * @param rowIndex the row index or <tt>-1</tt> if no row is selected
	 * @see DataModel#setRowIndex
	 */
	void setRowIndex(int rowIndex);

	/**
	 * Returns a read-only {@link Iterator} over the row data.
	 * @return a read-only iterator
	 * @see DataModel#iterator()
	 */
	Iterator<E> iterator();

}
