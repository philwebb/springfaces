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
package org.springframework.springfaces.page.model;

import java.util.Map;

import javax.faces.model.DataModel;

import org.springframework.springfaces.model.DataModelRowSet;
import org.springframework.springfaces.model.LazyDataLoader;
import org.springframework.springfaces.model.LazyDataModel;
import org.springframework.util.ObjectUtils;

/**
 * A {@link DataModel} that supports sorting, filtering and paging capabilities.
 * 
 * @author Phillip Webb
 * 
 * @param <E>
 */
public class PagedDataModel<E> extends LazyDataModel<E, PagedDataModelState> implements PagedDataRows<E> {

	/**
	 * Create a new {@link PagedDataModel} instance.
	 * @param loader the loader used to access {@link DataModelRowSet row data}
	 * @param state the state information associated with the data model
	 */
	public PagedDataModel(LazyDataLoader<E, PagedDataModelState> loader, PagedDataModelState state) {
		super(loader, state);
	}

	public int getPageSize() {
		return getState().getPageSize();
	}

	public void setPageSize(int pageSize) {
		if (getPageSize() != pageSize) {
			reset();
			getState().setPageSize(pageSize);
		}
	}

	public boolean isSortAscending() {
		return getState().isSortAscending();
	}

	public void setSortAscending(boolean sortAscending) {
		if (!ObjectUtils.nullSafeEquals(isSortAscending(), sortAscending)) {
			reset();
			getState().setSortAscending(sortAscending);
		}
	}

	public String getSortColumn() {
		return getState().getSortColumn();
	}

	public void setSortColumn(String sortColumn) {
		if (!ObjectUtils.nullSafeEquals(getSortColumn(), sortColumn)) {
			reset();
			getState().setSortColumn(sortColumn);
		}
	}

	public void toggleSort(String sortColumn) {
		if (ObjectUtils.nullSafeEquals(sortColumn, getSortColumn())) {
			setSortAscending(!isSortAscending());
		} else {
			setSortColumn(sortColumn);
			setSortAscending(true);
		}
	}

	public Map<String, String> getFilters() {
		return getState().getFilters();
	}

	public void setFilters(Map<String, String> filters) {
		if (!ObjectUtils.nullSafeEquals(getFilters(), filters)) {
			reset();
			getState().setFilters(filters);
		}
	}
}
