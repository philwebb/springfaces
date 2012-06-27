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
package org.springframework.springfaces.mvc.render;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link ViewArtifact}.
 * @author Phillip Webb
 */
public class ViewArtifactTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldNotHaveNullArtifact() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Artifact must not be null");
		new ViewArtifact(null);
	}

	@Test
	public void shouldHaveToString() throws Exception {
		ViewArtifact v = new ViewArtifact("test.xhtml");
		assertThat(v.toString(), is(equalTo("test.xhtml")));
	}

	@Test
	public void shouldSupportHashCodeAndEquals() throws Exception {
		ViewArtifact va1 = new ViewArtifact("a");
		ViewArtifact va2 = new ViewArtifact("a");
		ViewArtifact vb = new ViewArtifact("b");
		assertThat(va1, is(equalTo(va2)));
		assertThat(va1, is(not(equalTo(vb))));
		assertThat(va1.hashCode(), is(equalTo(va2.hashCode())));
		assertThat(va1.hashCode(), is(not(equalTo(vb.hashCode()))));
	}
}
