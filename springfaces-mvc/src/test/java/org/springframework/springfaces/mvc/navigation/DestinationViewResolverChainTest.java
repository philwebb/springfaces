package org.springframework.springfaces.mvc.navigation;

import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.web.servlet.View;

/**
 * Tests for {@link DestinationViewResolverChain}.
 * 
 * @author Phillip Webb
 */
public class DestinationViewResolverChainTest {

	private DestinationViewResolverChain chain = new DestinationViewResolverChain();

	private Locale locale = Locale.FRANCE;

	private Object destination = new Object();

	@Test
	public void shouldReturnNullWhenNullResolvers() throws Exception {
		assertNull(chain.resolveDestination(destination, locale));
	}

	@Test
	public void shouldReturnFirstSuitableResolver() throws Exception {
		View view = mock(View.class);
		List<DestinationViewResolver> resolvers = new ArrayList<DestinationViewResolver>();
		DestinationViewResolver r1 = mock(DestinationViewResolver.class);
		DestinationViewResolver r2 = mock(DestinationViewResolver.class);
		DestinationViewResolver r3 = mock(DestinationViewResolver.class);
		resolvers.add(r1);
		resolvers.add(r2);
		resolvers.add(r3);
		given(r2.resolveDestination(destination, locale)).willReturn(view);
		chain.setResolvers(resolvers);
		View resolved = chain.resolveDestination(destination, locale);
		assertSame(view, resolved);
		verify(r1).resolveDestination(destination, locale);
		verify(r3, never()).resolveDestination(resolved, locale);
	}
}
