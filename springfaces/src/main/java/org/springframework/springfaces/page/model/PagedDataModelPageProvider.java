package org.springframework.springfaces.page.model;

/**
 * Strategy interface that provides access to a single page of data.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public interface PagedDataModelPageProvider<E> {

	/**
	 * Returns a page for the given model state holder. The returned page must
	 * {@link PagedDataModelPage#containsRowIndex contain} row data for the
	 * {@link PagedDataModelStateHolder#getRowIndex() row index} specified in the stateHolder. Generally the page
	 * provider should also respect the {@link PagedDataModelStateHolder#getPageSize() page size} of the request,
	 * although this is not mandatory.
	 * @param stateHolder the state holder
	 * @return a page of data
	 */
	PagedDataModelPage<E> getPage(PagedDataModelStateHolder stateHolder);
}