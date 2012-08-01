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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.page.model.PagedDataModel;
import org.springframework.springfaces.page.model.PagedDataRows;
import org.springframework.springfaces.page.model.PrimeFacesPagedDataModel;

/**
 * Tests for {@link PagedPrimeFacesSupport}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class PagedPrimeFacesSupportTest {

	@Mock
	private PagedDataModel<Object> pagedDataRows;

	@After
	public void resetHasPrimeFaces() {
		PagedPrimeFacesSupport.setHasPrimeFaces(true);
	}

	@Test
	public void shouldNotWrapWithoutPrimeFaces() throws Exception {
		PagedPrimeFacesSupport.setHasPrimeFaces(false);
		PagedDataRows<Object> wrapped = PagedPrimeFacesSupport.getInstance().wrapPagedDataRows(this.pagedDataRows);
		assertThat(this.pagedDataRows, is(sameInstance(wrapped)));
	}

	@Test
	public void shouldWrapWithPrimeFaces() throws Exception {
		PagedDataRows<Object> wrapped = PagedPrimeFacesSupport.getInstance().wrapPagedDataRows(this.pagedDataRows);
		assertThat(wrapped, is(PrimeFacesPagedDataModel.class));
		wrapped.getPageSize();
		verify(this.pagedDataRows).getPageSize();
	}
}
