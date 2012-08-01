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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link ModelAndViewArtifact}.
 * 
 * @author Phillip Webb
 */
public class ModelAndViewArtifactTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ViewArtifact viewArtifact = new ViewArtifact("viewartifact");

	private Map<String, Object> model = new HashMap<String, Object>(Collections.singletonMap("m", "v"));

	@Test
	public void shouldNeedViewArtifact() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ViewArtifact must not be null");
		new ModelAndViewArtifact((ViewArtifact) null, null);
	}

	@Test
	public void shouldAllowOptionalModel() throws Exception {
		ModelAndViewArtifact m = new ModelAndViewArtifact(this.viewArtifact, null);
		assertThat(m.getViewArtifact(), is(this.viewArtifact));
		assertThat(m.getModel(), is(nullValue()));
	}

	@Test
	public void shouldCreateWithModel() throws Exception {
		ModelAndViewArtifact m = new ModelAndViewArtifact(this.viewArtifact, this.model);
		assertThat(m.getViewArtifact(), is(this.viewArtifact));
		assertThat(m.getModel(), is(this.model));
	}

	@Test
	public void shouldCreateFromStringViewArtifactAndModel() throws Exception {
		ModelAndViewArtifact m = new ModelAndViewArtifact("string", this.model);
		assertThat(m.getViewArtifact().toString(), is("string"));
		assertThat(m.getModel(), is(this.model));
	}

	@Test
	public void shouldCreateFromStringViewArtifact() throws Exception {
		ModelAndViewArtifact m = new ModelAndViewArtifact("string");
		assertThat(m.getViewArtifact().toString(), is("string"));
		assertThat(m.getModel(), is(nullValue()));
	}

	@Test
	public void shouldImplementHashCodeAndEquals() throws Exception {
		ModelAndViewArtifact m1 = new ModelAndViewArtifact(this.viewArtifact, this.model);
		ModelAndViewArtifact m2 = new ModelAndViewArtifact(this.viewArtifact, this.model);
		ModelAndViewArtifact m3 = new ModelAndViewArtifact(this.viewArtifact, null);
		assertThat(m1, is(equalTo(m1)));
		assertThat(m1, is(equalTo(m2)));
		assertThat(m1, is(not(equalTo(m3))));
		assertThat(m1.hashCode(), is(equalTo(m1.hashCode())));
		assertThat(m1.hashCode(), is(equalTo(m2.hashCode())));
		assertThat(m1.hashCode(), is(not(equalTo(m3.hashCode()))));
	}
}
