package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.Mapping;

//FIXME DC return NavigationOutcome or any other object to be treated as destination.  void or null will re-render current view.
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface NavigationMapping {

	// FIXME DC the actions that the mapping is for. If not specified will be derived the name of the method (eg click()
	// or onClick() will map to click)
	String[] value() default {};

	Class<?>[] handlers() default { void.class };

	// additional restriction used to limit based on actions
	String fromAction() default "";

	// FIXME support responseComplete = AUTO, NEVER, ALWAYS or @ResponseComplete

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
