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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import javax.faces.context.FacesContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link ImplicitNavigationOutcomeResolver}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class ImplicitNavigationOutcomeResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ImplicitNavigationOutcomeResolver resolver = new ImplicitNavigationOutcomeResolver();

	@Mock
	private FacesContext facesContext;

	@Mock
	private NavigationContext navigationContext;

	@Test
	public void shouldResolvePrefixedDefaultDestinationViewId() throws Exception {
		given(this.navigationContext.getDefaultDestinationViewId()).willReturn("spring:view");
		assertThat(this.resolver.canResolve(this.facesContext, this.navigationContext), is(true));
		NavigationOutcome outcome = this.resolver.resolve(this.facesContext, this.navigationContext);
		assertThat(outcome.getDestination(), is(equalTo((Object) "view")));
		assertThat(outcome.getImplicitModel(), is(nullValue()));
	}

	@Test
	public void shouldResolvePrefixedOutcome() throws Exception {
		given(this.navigationContext.getOutcome()).willReturn("spring:view");
		assertThat(this.resolver.canResolve(this.facesContext, this.navigationContext), is(true));
		NavigationOutcome outcome = this.resolver.resolve(this.facesContext, this.navigationContext);
		assertThat(outcome.getDestination(), is(equalTo((Object) "view")));
		assertThat(outcome.getImplicitModel(), is(nullValue()));
	}

	@Test
	public void shouldResolveCustomPrefix() throws Exception {
		this.resolver.setPrefix("springFaces:");
		given(this.navigationContext.getOutcome()).willReturn("springFaces:view");
		assertThat(this.resolver.canResolve(this.facesContext, this.navigationContext), is(true));
		NavigationOutcome outcome = this.resolver.resolve(this.facesContext, this.navigationContext);
		assertThat(outcome.getDestination(), is(equalTo((Object) "view")));
		assertThat(outcome.getImplicitModel(), is(nullValue()));
	}

	@Test
	public void shouldNotResolvedNonPrefixed() throws Exception {
		given(this.navigationContext.getOutcome()).willReturn("xspring:view");
		assertFalse(this.resolver.canResolve(this.facesContext, this.navigationContext));
	}

	@Test
	public void shouldRequireDestinationText() throws Exception {
		given(this.navigationContext.getOutcome()).willReturn("spring:");
		assertThat(this.resolver.canResolve(this.facesContext, this.navigationContext), is(true));
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage("The destination must be specified for an implicit MVC navigation prefixed 'spring:'");
		this.resolver.resolve(this.facesContext, this.navigationContext);
	}
}
