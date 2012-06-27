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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link NavigationOutcomeResolverChain}.
 * @author Phillip Webb
 */
public class NavigationOutcomeResolverChainTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private NavigationOutcomeResolverChain chain = new NavigationOutcomeResolverChain();

	@Mock
	private FacesContext facesContext;

	@Mock
	private NavigationContext navigationContext;

	@Mock
	private NavigationOutcomeResolver c1;

	@Mock
	private NavigationOutcomeResolver c2;

	@Mock
	private NavigationOutcomeResolver c3;

	private NavigationOutcome outcome = new NavigationOutcome(new Object());

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(this.navigationContext.getOutcome()).willReturn("outcome");
	}

	@Test
	public void shouldNotResolveIfNullChain() throws Exception {
		assertThat(this.chain.canResolve(this.facesContext, this.navigationContext), is(false));
	}

	@Test
	public void shouldResolveFromChain() throws Exception {
		this.chain.setResolvers(Arrays.asList(this.c1, this.c2, this.c3));
		given(this.c2.canResolve(this.facesContext, this.navigationContext)).willReturn(true);
		given(this.c2.resolve(this.facesContext, this.navigationContext)).willReturn(this.outcome);
		assertThat(this.chain.canResolve(this.facesContext, this.navigationContext), is(true));
		assertThat(this.chain.resolve(this.facesContext, this.navigationContext), is(equalTo(this.outcome)));
		verify(this.c1, never()).resolve(this.facesContext, this.navigationContext);
		verify(this.c2).resolve(this.facesContext, this.navigationContext);
		verify(this.c3, never()).resolve(this.facesContext, this.navigationContext);
	}

	@Test
	public void shouldFailIfResolvedWhenCanResolveIsFalse() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to find resolver for navigation outcome 'outcome'");
		this.chain.resolve(this.facesContext, this.navigationContext);
	}

	@Test
	public void shouldFailIfMoreThanOneInTheChainCanResolve() throws Exception {
		this.chain.setResolvers(Arrays.asList(this.c1, this.c2, this.c3));
		given(this.c2.canResolve(this.facesContext, this.navigationContext)).willReturn(true);
		given(this.c3.canResolve(this.facesContext, this.navigationContext)).willReturn(true);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Duplicate resolvers found for navigation outcome 'outcome'");
		this.chain.canResolve(this.facesContext, this.navigationContext);
	}

}
