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
		NavigationOutcome outcome = new NavigationOutcome(destination);
		assertEquals(destination, outcome.getDestination());
	}

	@Test
	public void shouldSetDestinationAndModel() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(destination, implicitModel);
		assertEquals(destination, outcome.getDestination());
		assertEquals(implicitModel, outcome.getImplicitModel());
	}

	@Test
	public void shouldRequireDestination() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		new NavigationOutcome(null, implicitModel);
	}

	@Test
	public void shouldSupportNullModel() throws Exception {
		NavigationOutcome outcome = new NavigationOutcome(destination, null);
		assertEquals(destination, outcome.getDestination());
		assertNull(outcome.getImplicitModel());
	}
}
