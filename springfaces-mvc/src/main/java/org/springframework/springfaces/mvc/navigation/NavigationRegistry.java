package org.springframework.springfaces.mvc.navigation;


public interface NavigationRegistry {

	void addMapping(NavigationMapping mapping);

	MappedNavigation getMappedNavigation(NavigationContext context);

}
