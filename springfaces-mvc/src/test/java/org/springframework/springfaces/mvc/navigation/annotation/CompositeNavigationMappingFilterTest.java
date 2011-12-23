package org.springframework.springfaces.mvc.navigation.annotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.navigation.NavigationContext;

/**
 * Tests for {@link CompositeNavigationMappingFilter}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeNavigationMappingFilterTest {

	@Rule
	public ExpectedException thown = ExpectedException.none();

	@Mock
	private NavigationContext context;

	@Test
	public void shouldNeedArray() throws Exception {
		this.thown.expect(IllegalArgumentException.class);
		this.thown.expectMessage("Filters must not be null");
		new CompositeNavigationMappingFilter((NavigationMappingFilter[]) null);
	}

	@Test
	public void shouldNeedNoNullElements() throws Exception {
		this.thown.expect(IllegalArgumentException.class);
		this.thown.expectMessage("Filters must not contain null elements");
		new CompositeNavigationMappingFilter((NavigationMappingFilter) null);
	}

	@Test
	public void shouldMatchIfAllMatch() throws Exception {
		NavigationMappingFilter f1 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f2 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f3 = mock(NavigationMappingFilter.class);
		given(f1.matches(this.context)).willReturn(true);
		given(f2.matches(this.context)).willReturn(true);
		given(f3.matches(this.context)).willReturn(true);
		CompositeNavigationMappingFilter composite = new CompositeNavigationMappingFilter(f1, f2, f3);
		assertTrue(composite.matches(this.context));
	}

	@Test
	public void shouldNotMatchIfAnyDoesNotMatch() throws Exception {
		NavigationMappingFilter f1 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f2 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f3 = mock(NavigationMappingFilter.class);
		given(f1.matches(this.context)).willReturn(true);
		given(f2.matches(this.context)).willReturn(false);
		given(f3.matches(this.context)).willReturn(true);
		CompositeNavigationMappingFilter composite = new CompositeNavigationMappingFilter(f1, f2, f3);
		assertFalse(composite.matches(this.context));
	}

}
