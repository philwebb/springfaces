package org.springframework.springfaces;

public interface FacesWrapperFactory<T> {

	public T newWrapper(T delegate);

}
