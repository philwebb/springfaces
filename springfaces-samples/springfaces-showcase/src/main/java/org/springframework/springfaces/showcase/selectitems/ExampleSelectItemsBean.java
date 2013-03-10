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
package org.springframework.springfaces.showcase.selectitems;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExampleSelectItemsBean implements Serializable {

	private Boolean booleanValue;

	private Set<Technology> technologies = new HashSet<Technology>();

	private List<String> strings = new ArrayList<String>();

	private Author author;

	private Author author2;

	private boolean panelVisible;

	public Boolean getBooleanValue() {
		return this.booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Set<Technology> getTechnologies() {
		return this.technologies;
	}

	public void setTechnologies(Set<Technology> technologies) {
		this.technologies = technologies;
	}

	public List<String> getStrings() {
		return this.strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	public Author getAuthor() {
		return this.author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Author getAuthor2() {
		return this.author2;
	}

	public void setAuthor2(Author author2) {
		this.author2 = author2;
	}

	public boolean isPanelVisible() {
		return this.panelVisible;
	}

	public void setPanelVisible(boolean panelVisible) {
		this.panelVisible = panelVisible;
	}
}
