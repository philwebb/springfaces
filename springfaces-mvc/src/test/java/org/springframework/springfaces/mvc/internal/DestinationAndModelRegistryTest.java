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
package org.springframework.springfaces.mvc.internal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;

/**
 * Tests for {@link DestinationAndModelRegistry}.
 * @author Phillip Webb
 */
public class DestinationAndModelRegistryTest {

	private DestinationAndModelRegistry registry = new DestinationAndModelRegistry();

	private FacesContext context;

	@Rule
	public ExpectedException thown = ExpectedException.none();

	@Before
	public void setup() {
		this.context = mock(FacesContext.class);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(this.context.getAttributes()).willReturn(attributes);
	}

	@Test
	public void shouldSupportMultipleItems() throws Exception {
		DestinationAndModel d1 = new DestinationAndModel(new NavigationOutcome(new Object()), (ActionEvent) null);
		DestinationAndModel d2 = new DestinationAndModel(new NavigationOutcome(new Object()), (ActionEvent) null);

		String k1 = this.registry.put(this.context, d1);
		String k2 = this.registry.put(this.context, d2);

		assertThat(k1, is(not(equalTo(k2))));
		assertThat(this.registry.get(this.context, k1), is(sameInstance(d1)));
		assertThat(this.registry.get(this.context, k2), is(sameInstance(d2)));
	}

	@Test
	public void shouldReturnNullWhenNotInRegistry() throws Exception {
		assertThat(this.registry.get(this.context, "missing"), is(nullValue()));
	}

	@Test
	public void shouldNeedFacesContext() throws Exception {
		DestinationAndModel d = new DestinationAndModel(new NavigationOutcome(new Object()), (ActionEvent) null);
		this.thown.expect(IllegalStateException.class);
		this.registry.put(null, d);
	}
}
