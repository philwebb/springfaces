package org.springframework.springfaces.model;

import java.util.List;

/**
 * Default implementation of {@link DataModelRowSet}.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public class DefaultDataModelRowSet<E> implements DataModelRowSet<E> {

	// FIXME more constructor
	// FIXME rename if possible

	private int offset;
	private int pageSize;
	private List<E> contents;
	private long totalRowCount;

	// FIXME deal with contents bigger than page size

	public DefaultDataModelRowSet(int offset, int pageSize, List<E> contents, long totalRowCount) {
		this.offset = offset;
		this.pageSize = pageSize;
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
