package org.springframework.springfaces.mvc.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
		assertTrue(this.resolver.canResolve(this.facesContext, this.navigationContext));
		NavigationOutcome outcome = this.resolver.resolve(this.facesContext, this.navigationContext);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldResolvePrefixedOutcome() throws Exception {
		given(this.navigationContext.getOutcome()).willReturn("spring:view");
		assertTrue(this.resolver.canResolve(this.facesContext, this.navigationContext));
		NavigationOutcome outcome = this.resolver.resolve(this.facesContext, this.navigationContext);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldResolveCustomPrefix() throws Exception {
		this.resolver.setPrefix("springFaces:");
		given(this.navigationContext.getOutcome()).willReturn("springFaces:view");
		assertTrue(this.resolver.canResolve(this.facesContext, this.navigationContext));
		NavigationOutcome outcome = this.resolver.resolve(this.facesContext, this.navigationContext);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldNotResolvedNonPrefixed() throws Exception {
		given(this.navigationContext.getOutcome()).willReturn("xspring:view");
		assertFalse(this.resolver.canResolve(this.facesContext, this.navigationContext));
	}

	@Test
	public void shouldRequireDestinationText() throws Exception {
		given(this.navigationContext.getOutcome()).willReturn("spring:");
		assertTrue(this.resolver.canResolve(this.facesContext, this.navigationContext));
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage("The destination must be specified for an implicit MVC navigation prefixed 'spring:'");
		this.resolver.resolve(this.facesContext, this.navigationContext);
	}
}
