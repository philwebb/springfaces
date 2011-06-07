package org.springframework.springfaces.mvc.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

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
	private NavigationContext context;

	@Test
	public void shouldResolvePrefixedDefaultDestinationViewId() throws Exception {
		given(context.getDefaultDestinationViewId()).willReturn("mvc:view");
		assertTrue(resolver.canResolve(context));
		NavigationOutcome outcome = resolver.resolve(context);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldResolvePrefixedOutcome() throws Exception {
		given(context.getOutcome()).willReturn("mvc:view");
		assertTrue(resolver.canResolve(context));
		NavigationOutcome outcome = resolver.resolve(context);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldResolveCustomPrefix() throws Exception {
		resolver.setPrefix("springFaces:");
		given(context.getOutcome()).willReturn("springFaces:view");
		assertTrue(resolver.canResolve(context));
		NavigationOutcome outcome = resolver.resolve(context);
		assertEquals("view", outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}

	@Test
	public void shouldNotResolvedNonPrefixed() throws Exception {
		given(context.getOutcome()).willReturn("xmvc:view");
		assertFalse(resolver.canResolve(context));
	}

	@Test
	public void shouldRequireDestinationText() throws Exception {
		given(context.getOutcome()).willReturn("mvc:");
		assertTrue(resolver.canResolve(context));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("The destination must be specified for an implicit MVC navigation prefixed 'mvc:'");
		resolver.resolve(context);
	}
}
