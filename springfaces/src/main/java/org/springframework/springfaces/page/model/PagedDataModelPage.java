package org.springframework.springfaces.page.model;


public interface PagedDataModelPage<T> {

	/**
	 * @return the total size of the page or -1
	 */
	long getRowCount();

	boolean containsRowIndex(int rowIndex);

	T getRowData(int rowIndex);

	public static final PagedDataModelPage<?> EMPTY = new PagedDataModelPage<Object>() {
		public long getRowCount() {
			return -1;
		}

		public Object getRowData(int rowIndex) {
			throw new NoRowAvailableException();
		}

		public boolean containsRowIndex(int rowIndex) {
			return false;
		}
	};

}
