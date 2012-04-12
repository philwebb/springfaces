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

import java.io.Serializable;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.util.Assert;

/**
 * A {@link DataModel} that supports lazy loading of row data. Users of this class must provide a {@link LazyDataLoader}
 * that will be used to load {@link DataModelRowSet row data} as required. This {@link DataModel} is not
 * {@link Serializable} as it is expected to be recreated on each JSF request. A {@link LazyDataModelState}
 * implementation must be provided to handle the data model state.
 * 
 * @param <E> The element type
 * @param <S> The data model state state
 * 
 * @author Phillip Webb
 */
public class LazyDataModel<E, S extends LazyDataModelState> extends DataModel<E> {

	private LazyDataLoader<E, S> loader;

	private S state;

	/**
	 * The current row set or <tt>null</tt>
	 */
	private DataModelRowSet<E> rowSet;

	/**
	 * The next row index that is likely to be read.
	 */
	private int nextRowIndex = 0;

	/**
	 * Create a new {@link LazyDataModel} instance.
	 * @param loader the loader used to access {@link DataModelRowSet row data}
	 * @param state the state information associated with the data model
	 */
	public LazyDataModel(LazyDataLoader<E, S> loader, S state) {
		Assert.notNull(loader, "Loader must not be null");
		Assert.notNull(state, "State must not be null");
		this.loader = loader;
		this.state = state;
	}

	/**
	 * Returns the state for the data model.
	 * @return the data model state
	 */
	protected final S getState() {
		return this.state;
	}

	/**
	 * Reset the row index and require that data is reloaded when next accessed
	 */
	protected void reset() {
		setRowIndex(-1);
		this.rowSet = null;
	}

	@Override
	public boolean isRowAvailable() {
		return getRowSet().isRowAvailable(getRowIndex());
	}

	@Override
	public int getRowCount() {
		Long rowCount = this.state.getLastLoadedTotalRowCount();
		if (rowCount == null) {
			rowCount = getAnyNonEmptyRowSet().getTotalRowCount();
		}
		Assert.state(rowCount != null, "The row count must not be null");
		Assert.state(rowCount >= -1, "The row count must be -1 or higher");
		if (rowCount > Integer.MAX_VALUE) {
			return -1;
		}
		return rowCount.intValue();
	}

	/**
	 * Reset any cached rowCount value.
	 */
	public void clearCachedRowCount() {
		clearCachedRowCount(0);
	}

	/**
	 * Reset any cached rowCount value and indicate the row index that will be next read. Providing the next row index
	 * allows the model to optimize data loads when {@link #getRowCount()} is called before {@link #setRowIndex(int)}.
	 * @param nextRowIndex the next row index that will be read or <tt>0</tt> if the next index is not know.
	 */
	public void clearCachedRowCount(int nextRowIndex) {
		this.state.setLastLoadedTotalRowCount(null);
		this.nextRowIndex = nextRowIndex;
	}

	@Override
	public E getRowData() {
		return getRowSet().getRowData(getRowIndex());
	}

	@Override
	public int getRowIndex() {
		return this.state.getRowIndex();
	}

	@Override
	public void setRowIndex(int rowIndex) {
		if (getRowIndex() != rowIndex) {
			Assert.isTrue(rowIndex >= -1, "RowIndex must not be less than -1");
			this.state.setRowIndex(rowIndex);
			fireDataModelListeners();
		}
	}

	private void fireDataModelListeners() {
		DataModelListener[] listeners = getDataModelListeners();
		if (listeners == null || listeners.length == 0) {
			return;
		}
		Object rowData = (isRowAvailable() ? getRowData() : null);
		DataModelEvent event = new DataModelEvent(this, getRowIndex(), rowData);
		for (DataModelListener listener : listeners) {
			if (listener != null) {
				listener.rowSelected(event);
			}
		}
	}

	@Override
	public Object getWrappedData() {
		return getRowSet();
	}

	@Override
	public void setWrappedData(Object o) {
		throw new UnsupportedOperationException("Unable to set wrapped data for LazyDataModel");
	}

	/**
	 * Returns a {@link DataModelRowSet} that contains data for the current {@link #getRowIndex() row index}. If the
	 * current row is -1 then an {@link DefaultDataModelRowSet#emptySet} set is returned.
	 * @return the row set for the current row or an empty row set
	 */
	private DataModelRowSet<E> getRowSet() {
		return getRowSet(getRowIndex());
	}

	/**
	 * Returns any non-empty row set. This method allows the total {@link #getRowCount() row count} to be obtained even
	 * if no current {@link #getRowIndex() row} is selected.
	 * @return a non-empty row set
	 */
	private DataModelRowSet<E> getAnyNonEmptyRowSet() {

		// If we are already on a row index we can use that to get the row set
		if (getRowIndex() != -1) {
			return getRowSet(getRowIndex());
		}

		// Attempt to get the row set for the next likely row index
		DataModelRowSet<E> rowSet = getRowSet(this.nextRowIndex);

		// If that fails, fall back to row 0
		if (this.nextRowIndex != 0 && !rowSet.isRowAvailable(this.nextRowIndex)) {
			rowSet = getRowSet(0);
		}

		return rowSet;
	}

	/**
	 * Returns the a {@link DataModelRowSet} containing the row data at the specified index. If the row index is -1 then
	 * an {@link DefaultDataModelRowSet#emptySet empty} row set is returned.
	 * @param rowIndex the row index
	 * @return the page for the specified row or an empty page
	 */
	private DataModelRowSet<E> getRowSet(int rowIndex) {
		if (rowIndex == -1) {
			return DefaultDataModelRowSet.<E> emptySet();
		}
		if (this.rowSet != null && this.rowSet.contains(rowIndex)) {
			return this.rowSet;
		}
		this.rowSet = loadRowSet(rowIndex);
		if (this.rowSet != null) {
			this.state.setLastLoadedTotalRowCount(this.rowSet.getTotalRowCount());
		}
		if (this.rowSet == null || !this.rowSet.contains(rowIndex)) {
			this.rowSet = DefaultDataModelRowSet.emptySet(rowIndex);
		}
		return this.rowSet;
	}

	/**
	 * Load a row set using the underlying loader.
	 * @param rowIndex the index to load
	 * @return a row data set
	 */
	private DataModelRowSet<E> loadRowSet(int rowIndex) {
		if (this.state.getRowIndex() == rowIndex) {
			return this.loader.getRows(this.state);
		}
		int previousRowIndex = this.state.getRowIndex();
		try {
			this.state.setRowIndex(rowIndex);
			return this.loader.getRows(this.state);
		} finally {
			this.state.setRowIndex(previousRowIndex);
		}
	}

}
