package org.springframework.springfaces.model;

import java.io.Serializable;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.springfaces.page.model.DataModelPageProvider;
import org.springframework.springfaces.page.model.PagedDataModelStateHolder;
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
 * @see DataModelPageProvider
 * @see PagedDataModelStateHolder
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

	@Override
	public boolean isRowAvailable() {
		return getRowSet().isRowAvailable(getRowIndex());
	}

	@Override
	public int getRowCount() {
		long rowCount = getAnyNonEmptyRowSet().getTotalRowCount();
		Assert.state(rowCount >= -1, "The row count must be -1 or higher");
		if (rowCount > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) rowCount;
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
	 * current row is -1 then an {@link EmptyDataModelRowSet empty} set is returned.
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
		return getRowSet(getRowIndex() == -1 ? 0 : getRowIndex());
	}

	/**
	 * Returns the a {@link DataModelRowSet} containing the row data at the specified index. If the row index is -1 then
	 * an {@link EmptyDataModelRowSet empty} row set is returned.
	 * @param rowIndex the row index
	 * @return the page for the specified row or an empty page
	 */
	private DataModelRowSet<E> getRowSet(int rowIndex) {
		if (rowSet == null || !rowSet.contains(rowIndex)) {
			rowSet = (rowIndex == -1 ? new EmptyDataModelRowSet<E>(-1) : loader.getRows(state));
			if (rowSet == null || !rowSet.contains(rowIndex)) {
				rowSet = EmptyDataModelRowSet.forRow(rowIndex);
			}
		}
		return rowSet;
	}
}
