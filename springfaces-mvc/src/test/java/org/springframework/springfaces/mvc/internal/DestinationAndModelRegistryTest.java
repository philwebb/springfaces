package org.springframework.springfaces.mvc.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
 * 
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
		given(context.getAttributes()).willReturn(attributes);
	}

	@Test
	public void shouldSupportMultipleItems() throws Exception {
		DestinationAndModel d1 = new DestinationAndModel(new NavigationOutcome(new Object()), (ActionEvent) null);
		DestinationAndModel d2 = new DestinationAndModel(new NavigationOutcome(new Object()), (ActionEvent) null);

		String k1 = registry.put(context, d1);
		String k2 = registry.put(context, d2);

		assertFalse(k1.equals(k2));
		assertSame(d1, registry.get(context, k1));
		assertSame(d2, registry.get(context, k2));
	}

	@Test
	public void shouldReturnNullWhenNotInRegistry() throws Exception {
		assertNull(registry.get(context, "missing"));
	}

	@Test
	public void shouldNeedFacesContext() throws Exception {
		DestinationAndModel d = new DestinationAndModel(new NavigationOutcome(new Object()), (ActionEvent) null);
		thown.expect(IllegalStateException.class);
		registry.put(null, d);
	}
}
