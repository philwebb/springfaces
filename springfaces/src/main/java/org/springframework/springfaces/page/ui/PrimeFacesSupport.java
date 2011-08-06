package org.springframework.springfaces.page.ui;

import org.springframework.springfaces.page.model.PagedDataModel;
import org.springframework.springfaces.page.model.PagedDataRows;
import org.springframework.springfaces.page.model.PrimeFacesPagedDataModel;
import org.springframework.util.ClassUtils;

/**
 * Support class that is used to dynamically enhance functionality when PrimeFaces is available.
 * 
 * @author Phillip Webb
 */
abstract class PrimeFacesSupport {

	/**
	 * Wrap a {@link PagedDataModel} with an instance suitable for PrimeFaces.
	 * @param <E> The element type
	 * @param pagedDataRows the model to wrap
	 * @return a wrapped data model or the original model if primefaces is unavailable.
	 */
	public abstract <E> PagedDataRows<E> wrapPagedDataRows(PagedDataModel<E> pagedDataRows);

	private static final boolean hasPrimeFaces = ClassUtils.isPresent("org.primefaces.model.LazyDataModel",
			PrimeFacesSupport.class.getClassLoader());

	private static PrimeFacesSupport instance;

	public static PrimeFacesSupport getInstance() {
		if (instance == null) {
			instance = (hasPrimeFaces ? new HasPrimeFaces() : new NoPrimeFaces());
		}
		return instance;
	}

	private static class HasPrimeFaces extends PrimeFacesSupport {
		@Override
		@SuppressWarnings("unchecked")
		public <E> PagedDataRows<E> wrapPagedDataRows(PagedDataModel<E> pagedDataRows) {
			return new PrimeFacesPagedDataModel(pagedDataRows);
		}
	}

	private static class NoPrimeFaces extends PrimeFacesSupport {
		@Override
		public <E> PagedDataRows<E> wrapPagedDataRows(PagedDataModel<E> pagedDataRows) {
			return pagedDataRows;
		}
	}
}
