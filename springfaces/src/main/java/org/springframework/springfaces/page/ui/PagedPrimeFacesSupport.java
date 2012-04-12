/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
abstract class PagedPrimeFacesSupport {

	/**
	 * Wrap a {@link PagedDataModel} with an instance suitable for PrimeFaces.
	 * @param <E> The element type
	 * @param pagedDataRows the model to wrap
	 * @return a wrapped data model or the original model if primefaces is unavailable.
	 */
	public abstract <E> PagedDataRows<E> wrapPagedDataRows(PagedDataModel<E> pagedDataRows);

	private static boolean hasPrimeFaces = ClassUtils.isPresent("org.primefaces.model.LazyDataModel",
			PagedPrimeFacesSupport.class.getClassLoader());

	private static PagedPrimeFacesSupport instance;

	public static PagedPrimeFacesSupport getInstance() {
		if (instance == null) {
			instance = (hasPrimeFaces ? new HasPrimeFaces() : new NoPrimeFaces());
		}
		return instance;
	}

	/**
	 * Override if primefaces is available. This is primarily to aid testing.
	 * @param hasPrimeFaces if spring data is available.
	 */
	static void setHasPrimeFaces(boolean hasPrimeFaces) {
		PagedPrimeFacesSupport.hasPrimeFaces = hasPrimeFaces;
		instance = null;
	}

	private static class HasPrimeFaces extends PagedPrimeFacesSupport {
		@Override
		@SuppressWarnings("unchecked")
		public <E> PagedDataRows<E> wrapPagedDataRows(PagedDataModel<E> pagedDataRows) {
			return new PrimeFacesPagedDataModel(pagedDataRows);
		}
	}

	private static class NoPrimeFaces extends PagedPrimeFacesSupport {
		@Override
		public <E> PagedDataRows<E> wrapPagedDataRows(PagedDataModel<E> pagedDataRows) {
			return pagedDataRows;
		}
	}
}
