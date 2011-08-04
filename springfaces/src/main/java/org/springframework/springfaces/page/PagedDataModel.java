package org.springframework.springfaces.page;

import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PagedDataModel<E> extends DataModel<E> implements Pageable {

	private PageableState state;

	private PageProvider<E> pageProvider;

	private Page<E> cachedPage;

	public PagedDataModel(PageableState state, PageProvider<E> pageProvider) {
		Assert.notNull(state, "state must not be null");
		Assert.notNull(pageProvider, "PageProvider must not be null");
		this.state = state;
		this.pageProvider = pageProvider;
	}

	@Override
	public boolean isRowAvailable() {
		return getPage().contains(getRowIndex());
	}

	@Override
	public int getRowCount() {
		long rowCount = getAnyNonEmptyPage().totalSize();
		// FIXME check bounds
		return (int) rowCount;
	}

	@Override
	public E getRowData() {
		return getPage().get(getRowIndex());
	}

	@Override
	public int getRowIndex() {
		return this.state.getRowIndex();
	}

	@Override
	public void setRowIndex(int rowIndex) {
		if (getRowIndex() != rowIndex) {
			Assert.isTrue(rowIndex >= -1, "rowIndex must not be less than -1");
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
		return getPage();
	}

	@Override
	public void setWrappedData(Object o) {
		throw new UnsupportedOperationException("Unable to set wrapped data for LazyPagedDataModel");
	}

	public int getPageSize() {
		return state.getPageSize();
	}

	public void setPageSize(int pageSize) {
		if (getPageSize() != pageSize) {
			setRowIndex(-1);
			state.setPageSize(pageSize);
		}
	}

	public Boolean getSortAscending() {
		return state.getSortAscending();
	}

	public void setSortAscending(Boolean sortAscending) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortAscending)) {
			setRowIndex(-1);
			state.setSortAscending(sortAscending);
		}
	}

	public String getSortColumn() {
		return state.getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortColumn)) {
			setRowIndex(-1);
			state.setSortColumn(sortColumn);
		}
	}

	public Map<String, String> getFilters() {
		return state.getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), filters)) {
			setRowIndex(-1);
			state.setFilters(filters);
		}
	}

	private Page<E> getPage() {
		return getPage(getRowIndex());
	}

	private Page<E> getAnyNonEmptyPage() {
		return getPage(getRowIndex() == -1 ? 0 : getRowIndex());
	}

	private Page<E> getPage(int rowIndex) {
		if (rowIndex == -1) {
			return emptyDataPage();
		}
		if (cachedPage == null || !cachedPage.contains(rowIndex)) {
			cachedPage = pageProvider.getPage(this);
		}
		return cachedPage;
	}

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

	@SuppressWarnings("unchecked")
	private static <E> Page<E> emptyDataPage() {
		return (Page<E>) EMPTY_PAGE;
	}

	public interface PageableState extends Pageable {
		void setRowIndex(int rowIndex);

		void setPageSize(int pageSize);

		void setSortAscending(Boolean sortAscending);

		void setSortColumn(String sortColumn);

		void setFilters(Map<String, String> filters);
	}

	public interface PageProvider<E> {
		Page<E> getPage(Pageable pageable);
	}
}
