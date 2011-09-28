package org.springframework.springfaces.model;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link DataModelRowSet}.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public class DefaultDataModelRowSet<E> implements DataModelRowSet<E> {

	private int offset;
	private int size;
	private List<E> contents;
	private long totalRowCount;

	public DefaultDataModelRowSet(List<E> contents) {
		this(0, contents);
	}

	public DefaultDataModelRowSet(int offset, List<E> contents) {
		this(offset, contents, UNKNOWN_TOTAL_ROW_COUNT);
	}

	public DefaultDataModelRowSet(int offset, List<E> contents, long totalRowCount) {
		this(offset, contents, contents.size(), totalRowCount);
	}

	public DefaultDataModelRowSet(int offset, List<E> contents, int size, long totalRowCount) {
		this.offset = offset;
		this.contents = contents;
		this.size = size;
		this.totalRowCount = totalRowCount;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public boolean contains(int rowIndex) {
		int ci = getContentsIndex(rowIndex);
		return (ci >= 0) && (ci < size);
	}

	public boolean isRowAvailable(int rowIndex) {
		int ci = getContentsIndex(rowIndex);
		return contains(rowIndex) && (ci >= 0) && (ci < contents.size());
	}

	public E getRowData(int rowIndex) throws NoRowAvailableException {
		if (!isRowAvailable(rowIndex)) {
			throw new NoRowAvailableException();
		}
		return contents.get(getContentsIndex(rowIndex));
	}

	private int getContentsIndex(int rowIndex) {
		return rowIndex - offset;
	}

	public static <E> DataModelRowSet<E> emptySet(int rowIndex) {
		return new DefaultDataModelRowSet<E>(0, Collections.<E> emptyList(), 1, UNKNOWN_TOTAL_ROW_COUNT);
	}
}
