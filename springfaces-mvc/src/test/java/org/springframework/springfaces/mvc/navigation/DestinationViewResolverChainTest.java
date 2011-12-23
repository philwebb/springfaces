package org.springframework.springfaces.mvc.navigation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests for {@link DestinationViewResolverChain}.
 * 
 * @author Phillip Webb
 */
public class DestinationViewResolverChainTest {

	private DestinationViewResolverChain chain = new DestinationViewResolverChain();

	private Locale locale = Locale.FRANCE;

	private Object destination = new Object();

	private SpringFacesModel model = new SpringFacesModel();

	@Test
	public void shouldReturnNullWhenNullResolvers() throws Exception {
		assertNull(this.chain.resolveDestination(this.destination, this.locale, this.model));
	}

	@Test
	public void shouldReturnFirstSuitableResolver() throws Exception {
		ModelAndView modelAndView = mock(ModelAndView.class);
		List<DestinationViewResolver> resolvers = new ArrayList<DestinationViewResolver>();
		DestinationViewResolver r1 = mock(DestinationViewResolver.class);
		DestinationViewResolver r2 = mock(DestinationViewResolver.class);
		DestinationViewResolver r3 = mock(DestinationViewResolver.class);
		resolvers.add(r1);
		resolvers.add(r2);
		resolvers.add(r3);
		given(r2.resolveDestination(this.destination, this.locale, this.model)).willReturn(modelAndView);
		this.chain.setResolvers(resolvers);
		ModelAndView resolved = this.chain.resolveDestination(this.destination, this.locale, this.model);
		assertSame(modelAndView, resolved);
		verify(r1).resolveDestination(this.destination, this.locale, this.model);
		verify(r3, never()).resolveDestination(resolved, this.locale, this.model);
	}
}
