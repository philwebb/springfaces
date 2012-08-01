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

import org.springframework.data.domain.Page;
import org.springframework.util.ClassUtils;

/**
 * Support class that is used to dynamically enhance functionality when Spring Data is available.
 * 
 * @author Phillip Webb
 */
abstract class PagedSpringDataSupport {

	/**
	 * Extend the specified {@link PageRequest} with the Spring Data <tt>Pageable</tt> interface.
	 * @param pageRequest the page request
	 * @return a new page request that also support <tt>Pageable</tt> or the original request if Spring Data is not
	 * available.
	 */
	public abstract PageRequest makePageable(PageRequest pageRequest);

	/**
	 * Extract the row count from the specified value. If the value is a Spring Data <tt>Page</tt> the row count will be
	 * extracted, otherwise the null is returned.
	 * @param value the value
	 * @return the row count or <tt>null</tt>
	 */
	public abstract Object getRowCountFromPage(Object value);

	/**
	 * Extract the content from the specified value. If the value is a Spring Data <tt>Page</tt> the contexts will be
	 * extracted, otherwise the original value is returned.
	 * @param value
	 * @return the contents or the original value
	 */
	public abstract Object getContentFromPage(Object value);

	private static boolean hasSpringData = ClassUtils.isPresent("org.springframework.data.domain.Page",
			PagedSpringDataSupport.class.getClassLoader());

	private static PagedSpringDataSupport instance;

	public static PagedSpringDataSupport getInstance() {
		if (instance == null) {
			instance = (hasSpringData ? new HasSpringData() : new NoSpringData());
		}
		return instance;
	}

	/**
	 * Override if spring data is available. This is primarily to aid testing.
	 * @param hasSpringData if spring data is available.
	 */
	static void setHasSpringData(boolean hasSpringData) {
		PagedSpringDataSupport.hasSpringData = hasSpringData;
		instance = null;
	}

	@SuppressWarnings("rawtypes")
	private static class HasSpringData extends PagedSpringDataSupport {
		@Override
		public PageRequest makePageable(PageRequest pageRequest) {
			return new SpringDataPageRequest(pageRequest);
		}

		@Override
		public Object getRowCountFromPage(Object value) {
			if (value instanceof Page) {
				return ((Page) value).getTotalElements();
			}
			return null;
		}

		@Override
		public Object getContentFromPage(Object value) {
			if (value instanceof Page) {
				return ((Page) value).getContent();
			}
			return value;
		}
	}

	private static class NoSpringData extends PagedSpringDataSupport {
		@Override
		public PageRequest makePageable(PageRequest pageRequest) {
			return pageRequest;
		}

		@Override
		public Object getRowCountFromPage(Object value) {
			return null;
		}

		@Override
		public Object getContentFromPage(Object value) {
			return value;
		}
	}
}
