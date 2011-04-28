package org.springframework.springfaces.mvc.navigation.annotation;

public @interface NavigationCase {

	String on() default "";

	String fromAction() default "";

	String to() default "";

	String condition() default "";

	boolean redirect() default true;

	String[] parameters() default {};
}
