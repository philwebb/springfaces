package org.springframework.springfaces.mvc.navigation;

public @interface NavigationCase {

	String on();

	String fromAction();

	String to();

	String condition();

	String redirect();

	String redirectParameters();
}
