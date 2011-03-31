package org.springframework.springfaces.mvc.navigation.annotation;

public @interface NavigationCase {

	String on();

	String fromAction();

	String to();

	String condition();

	String redirect();

	String redirectParameters();
}
