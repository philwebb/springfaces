package org.springframework.springfaces.mvc.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
 * 
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
		given(navigationContext.getOutcome()).willReturn("outcome");
	}

	@Test
	public void shouldNotResolveIfNullChain() throws Exception {
		assertFalse(chain.canResolve(facesContext, navigationContext));
	}

	@Test
	public void shouldResolveFromChain() throws Exception {
		chain.setResolvers(Arrays.asList(c1, c2, c3));
		given(c2.canResolve(facesContext, navigationContext)).willReturn(true);
		given(c2.resolve(facesContext, navigationContext)).willReturn(outcome);
		assertTrue(chain.canResolve(facesContext, navigationContext));
		assertEquals(outcome, chain.resolve(facesContext, navigationContext));
		verify(c1, never()).resolve(facesContext, navigationContext);
		verify(c2).resolve(facesContext, navigationContext);
		verify(c3, never()).resolve(facesContext, navigationContext);
	}

	@Test
	public void shouldFailIfResolvedWhenCanResolveIsFalse() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to find resolver for navigation outcome 'outcome'");
		chain.resolve(facesContext, navigationContext);
	}

	@Test
	public void shouldFailIfMoreThanOneInTheChainCanResolve() throws Exception {
		chain.setResolvers(Arrays.asList(c1, c2, c3));
		given(c2.canResolve(facesContext, navigationContext)).willReturn(true);
		given(c3.canResolve(facesContext, navigationContext)).willReturn(true);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Duplicate resolvers found for navigation outcome 'outcome'");
		chain.canResolve(facesContext, navigationContext);
	}

}
