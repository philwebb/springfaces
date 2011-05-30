package org.springframework.springfaces.mvc.navigation;

import java.util.List;
import java.util.Locale;

import org.springframework.web.servlet.View;

/**
 * {@link DestinationViewResolver} that allows several resolvers to be chained together. The first resolver that returns
 * a <tt>non-null</tt> {@link View} is used.
 * 
 * @author Phillip Webb
 */
public class DestinationViewResolverChain implements DestinationViewResolver {

	private List<DestinationViewResolver> resolvers;

	public View resolveDestination(Object destination, Locale locale) throws Exception {
		if (resolvers != null) {
			for (DestinationViewResolver resolver : resolvers) {
				View view = resolver.resolveDestination(destination, locale);
				if (view != null) {
					return view;
				}
			}
		}
		return null;
	}

	/**
	 * Set the list of resolvers that will be used when resolving a destination.
	 * @param resolvers the list of resolvers
	 */
	public void setResolvers(List<DestinationViewResolver> resolvers) {
		this.resolvers = resolvers;
	}
}
