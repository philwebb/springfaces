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
