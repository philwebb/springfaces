package org.springframework.springfaces.mvc.navigation.annotation;


//FIXME DC return NavigationOutcome or any other object to be treated as destination.  void or null will re-render current view.
public @interface NavigationMapping {

	// FIXME DC the actions that the mapping is for. If not specified will be derived the name of the method (eg click()
	// or onClick() will map to click)
	String[] value() default {};

	// additional restriction used to limit based on actions
	String fromAction() default "";

	// FIXME define this
	// FIXME write a listener to support it

	// FIXME
	// ModelMap
	// Map
	// FacesContext
	// ExternalContext
	// Value
	// Model elements by type?

}
