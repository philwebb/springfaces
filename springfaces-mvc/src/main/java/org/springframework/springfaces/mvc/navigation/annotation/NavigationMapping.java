package org.springframework.springfaces.mvc.navigation.annotation;

public @interface NavigationMapping {

	String[] value() default {};

	String fromAction() default "";

	// FIXME define this
	// FIXME write a listener to support it

}
