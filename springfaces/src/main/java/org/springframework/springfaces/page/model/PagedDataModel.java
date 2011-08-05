package org.springframework.springfaces.page.model;

import java.io.Serializable;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.springfaces.model.PagedDataRows;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * A {@link DataModel} that supports {@link PagedDataRows paged rows}. Users of this class must provide a
 * {@link PagedDataModelPageProvider} that load data one page at a time. This {@link DataModel} is not
 * {@link Serializable} as it is expected to be recreated on each JSF request. A {@link PagedDataModelStateHolder}
 * implementation must be provided to handle state saving.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public class PagedDataModel<E> extends DataModel<E> implements PagedDataRows<E> {

	private PagedDataModelPageProvider<E> provider;

	private PagedDataModelStateHolder stateHolder;

	private PagedDataModelPage<E> cachedPage;

	/**
	 * Create a new {@link PagedDataModel} instance.
	 * @param pageProvider a page provider that will allow row data to be access one page at a time
	 * @param stateHolder allows access to state information associated with the data model
	 */
	public PagedDataModel(PagedDataModelPageProvider<E> pageProvider, PagedDataModelStateHolder stateHolder) {
		Assert.notNull(pageProvider, "PageProvider must not be null");
		Assert.notNull(stateHolder, "StateHolder must not be null");
		this.provider = pageProvider;
		this.stateHolder = stateHolder;
	}

	@Override
	public boolean isRowAvailable() {
		return getPage().isRowAvailable(getRowIndex());
	}

	@Override
	public int getRowCount() {
		long rowCount = getAnyNonEmptyPage().getTotalRowCount();
		Assert.state(rowCount >= -1, "The row count must be -1 or higher");
		if (rowCount > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) rowCount;
	}

	@Override
	public E getRowData() {
		return getPage().getRowData(getRowIndex());
	}

	@Override
	public int getRowIndex() {
		return this.stateHolder.getRowIndex();
	}

	@Override
	public void setRowIndex(int rowIndex) {
		if (getRowIndex() != rowIndex) {
			Assert.isTrue(rowIndex >= -1, "RowIndex must not be less than -1");
			this.stateHolder.setRowIndex(rowIndex);
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
		return stateHolder.getPageSize();
	}

	public void setPageSize(int pageSize) {
		if (getPageSize() != pageSize) {
			setRowIndex(-1);
			stateHolder.setPageSize(pageSize);
		}
	}

	public boolean getSortAscending() {
		return stateHolder.isSortAscending();
	}

	public void setSortAscending(boolean sortAscending) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortAscending)) {
			setRowIndex(-1);
			stateHolder.setSortAscending(sortAscending);
		}
	}

	public String getSortColumn() {
		return stateHolder.getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), sortColumn)) {
			setRowIndex(-1);
			stateHolder.setSortColumn(sortColumn);
		}
	}

	public Map<String, String> getFilters() {
		return stateHolder.getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		if (!ObjectUtils.nullSafeEquals(getSortAscending(), filters)) {
			setRowIndex(-1);
			stateHolder.setFilters(filters);
		}
	}

	/**
	 * Returns a page that contains data for the current {@link #getRowIndex() row index}. If the current row is -1 then
	 * an {@link EmptyPagedDataModelPage#getInstance() empty} page is returned.
	 * @return the page for the current row or an empty page
	 */
	private PagedDataModelPage<E> getPage() {
		return getPage(getRowIndex());
	}

	/**
	 * Returns any non-empty page. This method allows the total {@link #getRowCount() row count} to be obtained even if
	 * no current {@link #getRowIndex() row} is selected.
	 * @return a non-empty page
	 */
	private PagedDataModelPage<E> getAnyNonEmptyPage() {
		return getPage(getRowIndex() == -1 ? 0 : getRowIndex());
	}

	/**
	 * Returns the a page containing the row data at the specified index. If the row index is -1 then an
	 * {@link EmptyPagedDataModelPage#getInstance() empty} page is returned.
	 * @param rowIndex the row index
	 * @return the page for the specified row or an empty page
	 */
	private PagedDataModelPage<E> getPage(int rowIndex) {
		if (cachedPage == null || !cachedPage.containsRowIndex(rowIndex)) {
			cachedPage = (rowIndex == -1 ? new EmptyPagedDataModelPage<E>(-1) : provider.getPage(stateHolder));
			Assert.state(cachedPage != null, "No page returned from provider for row index " + rowIndex);
			if (!cachedPage.containsRowIndex(rowIndex)) {
				// The returned page does not contain the requested row, cache an empty page to save retries
				cachedPage = new EmptyPagedDataModelPage<E>(rowIndex);
			}
		}
		return cachedPage;
	}
}
