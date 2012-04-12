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
package org.springframework.springfaces.mvc.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link NavigationOutcome}.
 * 
 * @author Phillip Webb
 */
public class NavigationOutcomeTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private String destination = "destination";

	private Map<String, Object> implicitModel = Collections.<String, Object> singletonMap("k", "v");

	@Test
	public void shouldSetDestination() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(this.destination);
		assertEquals(this.destination, outcome.getDestination());
	}

	@Test
	public void shouldSetDestinationAndModel() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(this.destination, this.implicitModel);
		assertEquals(this.destination, outcome.getDestination());
		assertEquals(this.implicitModel, outcome.getImplicitModel());
	}

	@Test
	public void shouldRequireDestination() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		new NavigationOutcome(null, this.implicitModel);
	}

	@Test
	public void shouldSupportNullModel() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(this.destination, null);
		assertEquals(this.destination, outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}
}
