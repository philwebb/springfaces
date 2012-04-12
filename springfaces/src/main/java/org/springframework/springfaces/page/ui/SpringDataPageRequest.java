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

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;

/**
 * Extends a {@link PageRequest} to support the Spring Data {@link Pageable} interface.
 * 
 * @author Phillip Webb
 */
public class SpringDataPageRequest implements PageRequest, Pageable {

	private PageRequest pageRequest;

	public SpringDataPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}

	public int getPageNumber() {
		return this.pageRequest.getPageNumber();
	}

	public int getPageSize() {
		return this.pageRequest.getPageSize();
	}

	public int getOffset() {
		return this.pageRequest.getOffset();
	}

	public String getSortColumn() {
		return this.pageRequest.getSortColumn();
	}

	public boolean isSortAscending() {
		return this.pageRequest.isSortAscending();
	}

	public Map<String, String> getFilters() {
		return this.pageRequest.getFilters();
	}

	public Sort getSort() {
		if (StringUtils.hasLength(getSortColumn())) {
			return new Sort(getSortDirection(), getSortColumn());
		}
		return null;
	}

	private Direction getSortDirection() {
		return (isSortAscending() ? Direction.ASC : Direction.DESC);
	}

}
