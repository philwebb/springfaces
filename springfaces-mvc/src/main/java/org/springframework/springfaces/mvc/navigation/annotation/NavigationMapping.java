package org.springframework.springfaces.mvc.navigation.annotation;

public @interface NavigationMapping {

	String[] value() default {};

	String fromAction() default "";

}
