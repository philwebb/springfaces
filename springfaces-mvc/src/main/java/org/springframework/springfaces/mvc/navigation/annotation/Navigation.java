package org.springframework.springfaces.mvc.navigation.annotation;

public @interface Navigation {

	String fromViewId() default "";

	Class<?> fromController() default void.class;

	public NavigationCase[] value();

}
