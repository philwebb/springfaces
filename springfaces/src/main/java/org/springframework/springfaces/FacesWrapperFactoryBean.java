package org.springframework.springfaces;

public interface FacesWrapperFactoryBean<T> {

	public T newWrapper(T delegate);

}
