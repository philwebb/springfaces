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

/**
 * Strategy interface that is used by the {@link LazyDataModel} to load row data.
 * 
 * @author Phillip Webb
 * @param <E> The element type
 * @param <S> The lazy data model state
 */
public interface LazyDataLoader<E, S extends LazyDataModelState> {

	/**
	 * Returns row data appropriate for the given sate object. If the underlying source does not contains the
	 * {@link LazyDataModelState#getRowIndex row index} from the specified state then <tt>null</tt> should be returned.
	 * @param state the state holder
	 * @return a page of data or <tt>null</tt>.
	 */
	DataModelRowSet<E> getRows(S state);
}
