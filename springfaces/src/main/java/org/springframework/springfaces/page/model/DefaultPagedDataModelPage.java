package org.springframework.springfaces.page.model;

import java.util.List;

import org.springframework.springfaces.model.NoRowAvailableException;
import org.springframework.springfaces.page.ui.PageRequest;

/**
 * Default implementation of {@link PagedDataModelContent}.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public class DefaultPagedDataModelPage<E> implements PagedDataModelContent<E> {

	private int offset;
	private int pageSize;
	private List<E> contents;
	private long totalRowCount;

	// FIXME additional constructor
	// FIXME deal with contents bigger than page size

	/**
	 * Create a new {@link DefaultPagedDataModelPage} instance.
	 * @param request the page request
	 * @param contents the contents of the data page
	 * @param totalRowCount the total row count or <tt>-1</tt> if unknown
	 */
	public DefaultPagedDataModelPage(PageRequest request, List<E> contents, long totalRowCount) {
		this.offset = request.getOffset();
		this.pageSize = request.getPageSize();
		this.contents = contents;
		this.totalRowCount = totalRowCount;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public boolean contains(int rowIndex) {
		return getContentsIndex(rowIndex) < pageSize;
	}

	public boolean isRowAvailable(int rowIndex) {
		return getContentsIndex(rowIndex) < contents.size();
	}

	public E getRowData(int rowIndex) throws NoRowAvailableException {
		if (!contains(rowIndex)) {
			throw new NoRowAvailableException();
		}
		return contents.get(getContentsIndex(rowIndex));
	}

	private int getContentsIndex(int rowIndex) {
		return rowIndex - offset;
	}
}
