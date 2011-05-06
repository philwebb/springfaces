package org.springframework.springfaces.mvc.navigation;

import java.util.Locale;

import org.springframework.web.servlet.View;

/**
 * @author Phillip Webb
 */
public interface DestinationViewResolver {

	View resolveDestination(Object destination, Locale locale);

}
