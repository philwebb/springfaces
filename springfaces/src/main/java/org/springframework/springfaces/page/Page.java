package org.springframework.springfaces.page;

public interface Page<T> {

	/**
	 * @return the total size of the page or -1
	 */
	long totalSize();

	boolean contains(int index);

	T get(int index);

}
