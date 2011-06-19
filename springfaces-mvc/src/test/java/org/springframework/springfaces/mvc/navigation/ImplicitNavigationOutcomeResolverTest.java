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
		given(navigationContext.getDefaultDestinationViewId()).willReturn("spring:view");
		assertTrue(resolver.canResolve(facesContext, navigationContext));
		NavigationOutcome outcome = resolver.resolve(facesContext, navigationContext);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldResolvePrefixedOutcome() throws Exception {
		given(navigationContext.getOutcome()).willReturn("spring:view");
		assertTrue(resolver.canResolve(facesContext, navigationContext));
		NavigationOutcome outcome = resolver.resolve(facesContext, navigationContext);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldResolveCustomPrefix() throws Exception {
		resolver.setPrefix("springFaces:");
		given(navigationContext.getOutcome()).willReturn("springFaces:view");
		assertTrue(resolver.canResolve(facesContext, navigationContext));
		NavigationOutcome outcome = resolver.resolve(facesContext, navigationContext);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldNotResolvedNonPrefixed() throws Exception {
		given(navigationContext.getOutcome()).willReturn("xspring:view");
		assertFalse(resolver.canResolve(facesContext, navigationContext));
	}

	@Test
	public void shouldRequireDestinationText() throws Exception {
		given(navigationContext.getOutcome()).willReturn("spring:");
		assertTrue(resolver.canResolve(facesContext, navigationContext));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("The destination must be specified for an implicit MVC navigation prefixed 'spring:'");
		resolver.resolve(facesContext, navigationContext);
	}
}
