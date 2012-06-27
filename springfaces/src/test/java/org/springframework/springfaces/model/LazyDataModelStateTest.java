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
package org.springframework.springfaces.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link LazyDataModelState}.
 * @author Phillip Webb
 */
public class LazyDataModelStateTest {

	@Test
	public void shouldDefaultToNoRow() throws Exception {
		LazyDataModelState state = new LazyDataModelState();
		assertThat(state.getRowIndex(), is(-1));
	}

	@Test
	public void shouldSupportSet() throws Exception {
		LazyDataModelState state = new LazyDataModelState();
		state.setRowIndex(10);
		assertThat(state.getRowIndex(), is(10));
	}
}
