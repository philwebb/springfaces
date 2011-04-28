package org.springframework.springfaces.mvc.navigation;


public interface NavigationContext {

	Object getHandler();

	String getFromAction();

	String getOutcome();

	boolean isPreEmptive();
}
