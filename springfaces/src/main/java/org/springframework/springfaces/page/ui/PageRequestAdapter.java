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

import java.util.Collections;
import java.util.Map;

import org.springframework.springfaces.page.model.PagedDataModelState;
import org.springframework.util.Assert;

/**
 * Adapter class that converts {@link PagedDataModelState} to a {@link PageRequest}.
 * @author Phillip Webb
 */
class PageRequestAdapter implements PageRequest {

	private PagedDataModelState state;

	public PageRequestAdapter(PagedDataModelState state) {
		Assert.notNull(state, "State must not be null");
		this.state = state;
	}

	public int getPageNumber() {
		return this.state.getRowIndex() / getPageSize();
	}

	public int getPageSize() {
		return this.state.getPageSize();
	}

	public int getOffset() {
		return getPageNumber() * getPageSize();
	}

	public String getSortColumn() {
		return this.state.getSortColumn();
	}

	public boolean isSortAscending() {
		return this.state.isSortAscending();
	}

	public Map<String, String> getFilters() {
		Map<String, String> filters = this.state.getFilters();
		return (filters == null ? Collections.<String, String> emptyMap() : filters);
	}
}
