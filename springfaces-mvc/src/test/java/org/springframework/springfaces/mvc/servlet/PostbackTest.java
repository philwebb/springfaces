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
package org.springframework.springfaces.mvc.servlet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.springfaces.mvc.render.ViewArtifact;

/**
 * Tests for {@link Postback}.
 * @author Phillip Webb
 */
public class PostbackTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ViewArtifact viewArtifact = new ViewArtifact("test.xhtml");

	private Object handler = new Object();

	@Test
	public void shouldNeedViewArtifact() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ViewArtifact must not be null");
		new Postback(null, this.handler);
	}

	@Test
	public void shouldNeedHandler() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Handler must not be null");
		new Postback(this.viewArtifact, null);
	}

	@Test
	public void shouldContainViewArtifactAndHandler() throws Exception {
		Postback p = new Postback(this.viewArtifact, this.handler);
		assertThat(p.getViewArtifact(), is(sameInstance(this.viewArtifact)));
		assertThat(p.getHandler(), is(sameInstance(this.handler)));
	}
}
