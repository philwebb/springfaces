package org.springframework.springfaces.page;


public interface Page<T> {

	long totalSize();

	boolean contains(int index);

	T get(int index);

}
