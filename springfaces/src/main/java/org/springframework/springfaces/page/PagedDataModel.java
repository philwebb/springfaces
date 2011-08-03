package org.springframework.springfaces.page;

import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PagedDataModel<E> extends DataModel<E> implements MutablePageContext {

	private static final Page<?> EMPTY_PAGE = new Page<Object>() {
		public long totalSize() {
			return -1;
		}

		public Object get(int rowIndex) {
			throw new NoRowAvailableException();
		}

		public boolean contains(int rowIndex) {
			return false;
		}
	};

	private Provider<E> provider;

	@SuppressWarnings("unchecked")
	private static <E> Page<E> emptyDataPage() {
		return (Page<E>) EMPTY_PAGE;
	}

	private int rowIndex;

	private Page<E> rowData;

	public PagedDataModel() {
		super();
		this.rowIndex = -1;
	}

	@Override
	public boolean isRowAvailable() {
		return getRowData(rowIndex).contains(rowIndex);
	}

	@Override
	public int getRowCount() {
		long totalElements = getRowData(rowIndex == -1 ? 0 : rowIndex).totalSize();
		// FIXME check bounds
		return (int) totalElements;
	}

	@Override
	public E getRowData() {
		return getRowData(rowIndex).get(rowIndex);
	}

	@Override
	public int getRowIndex() {
		return rowIndex;
	}

	@Override
	public void setRowIndex(int rowIndex) {
		if (this.rowIndex != rowIndex) {
			Assert.isTrue(rowIndex >= -1, "rowIndex must not be less than -1");
			int oldRowIndex = this.rowIndex;
			this.rowIndex = rowIndex;
			fireDataModelListeners(oldRowIndex);
		}
	}

	private void fireDataModelListeners(int oldRowIndex) {
		DataModelListener[] listeners = getDataModelListeners();
		if (listeners == null || listeners.length == 0) {
			return;
		}
		Object rowData = (isRowAvailable() ? getRowData() : null);
		DataModelEvent event = new DataModelEvent(this, rowIndex, rowData);
		for (DataModelListener listener : listeners) {
			if (listener != null) {
				listener.rowSelected(event);
			}
		}
	}

	@Override
	public Object getWrappedData() {
		return getRowData(rowIndex);
	}

	@Override
	public void setWrappedData(Object o) {
		throw new UnsupportedOperationException("Unable to set wrapped data for LazyPagedDataModel");
	}

	public int getPageSize() {
		return provider.getPageSize();
	}

	public void setPageSize(int pageSize) {
		if (getPageSize() != pageSize) {
			setRowIndex(-1);
			provider.setPageSize(pageSize);
		}
	}

	public Boolean getSortAscending() {
		return provider.getSortAscending();
	}

	public void setSortAscending(Boolean sortAscending) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortAscending)) {
			setRowIndex(-1);
			provider.setSortAscending(sortAscending);
		}
	}

	public String getSortColumn() {
		return provider.getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortColumn)) {
			setRowIndex(-1);
			provider.setSortColumn(sortColumn);
		}
	}

	public Map<String, String> getFilters() {
		return provider.getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), filters)) {
			setRowIndex(-1);
			provider.setFilters(filters);
		}
	}

	private Page<E> getRowData(int rowIndex) {
		if (rowIndex == -1) {
			return emptyDataPage();
		}
		if (rowData == null || !rowData.contains(rowIndex)) {
			rowData = provider.getPage(rowIndex);
		}
		return rowData;
	}

	public interface Provider<E> extends MutablePageContext {
		Page<E> getPage(int index);
	}
}
