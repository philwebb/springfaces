package org.springframework.springfaces.page.model;

import java.util.List;

import org.springframework.springfaces.page.ui.PageRequest;

public class DefaultPagedDataModelPage<E> implements PagedDataModelPage<E> {

	private int offset;
	private int pageSize;
	private List<E> contents;
	private long totalRowCount;

	public DefaultPagedDataModelPage(PageRequest request, List<E> contents, long totalRowCount) {
		this.offset = request.getOffset();
		this.pageSize = request.getPageSize();
		this.contents = contents;
		this.totalRowCount = totalRowCount;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public boolean containsRowIndex(int rowIndex) {
		return getContentsIndex(rowIndex) < pageSize;
	}

	public boolean isRowAvailable(int rowIndex) {
		return getContentsIndex(rowIndex) < contents.size();
	}

	public E getRowData(int rowIndex) throws NoRowAvailableException {
		if (!containsRowIndex(rowIndex)) {
			throw new NoRowAvailableException();
		}
		return contents.get(getContentsIndex(rowIndex));
	}

	private int getContentsIndex(int rowIndex) {
		return rowIndex - offset;
	}
}
