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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Example repository. In a real appplication this could be a Spring Data repository.
 * 
 * @author Phillip Webb
 */
@Component
public class AuthorRepository {

	private static List<Author> AUTHORS;
	static {
		List<Author> authors = new ArrayList<Author>();
		long i = 0L;
		authors.add(new Author(i++, "Rod", "Johnson"));
		authors.add(new Author(i++, "Juergen", "Hoeller"));
		authors.add(new Author(i++, "Keith", "Donald"));
		authors.add(new Author(i++, "Colin", "Sampaleanu"));
		authors.add(new Author(i++, "Rob", "Harrop"));
		authors.add(new Author(i++, "Alef", "Arendsen"));
		authors.add(new Author(i++, "Thomas", "Risberg"));
		authors.add(new Author(i++, "Darren", "Davison"));
		authors.add(new Author(i++, "Dmitriy", "Kopylenko"));
		authors.add(new Author(i++, "Mark", "Pollack"));
		authors.add(new Author(i++, "Thierry", "Templier"));
		authors.add(new Author(i++, "Erwin", "Vervaet"));
		authors.add(new Author(i++, "Portia", "Tung"));
		authors.add(new Author(i++, "Ben", "Hale"));
		authors.add(new Author(i++, "Adrian", "Colyer"));
		authors.add(new Author(i++, "John", "Lewis"));
		authors.add(new Author(i++, "Costin", "Leau"));
		authors.add(new Author(i++, "Mark", "Fisher"));
		authors.add(new Author(i++, "Sam", "Brannen"));
		authors.add(new Author(i++, "Ramnivas", "Laddad"));
		authors.add(new Author(i++, "Arjen", "Poutsma"));
		authors.add(new Author(i++, "Chris", "Beams"));
		authors.add(new Author(i++, "Tareq", "Abedrabbo"));
		authors.add(new Author(i++, "Andy", "Clement"));
		authors.add(new Author(i++, "Dave", "Syer"));
		authors.add(new Author(i++, "Oliver", "Gierke"));
		authors.add(new Author(i++, "Rossen", "Stoyanchev"));
		AUTHORS = Collections.unmodifiableList(authors);
	}

	public List<Author> getAll() {
		return AUTHORS;
	}
}
