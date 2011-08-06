package org.springframework.springfaces.model;

/**
 * Strategy interface that is used by the {@link LazyDataModel} to load row data.
 * 
 * @author Phillip Webb
 * 
 * @param <E> The element type
 * @param <S> The lazy data model state
 */
public interface LazyDataLoader<E, S extends LazyDataModelState> {

	/**
	 * Returns row data appropriate for the given sate object. If the underlying source does not contains the
	 * {@link LazyDataModelState#getRowIndex row index} from the specified state then <tt>null</tt> should be returned.
	 * @param state the state holder
	 * @return a page of data or <tt>null</tt>.
	 */
	DataModelRowSet<E> getRows(S state);
}
