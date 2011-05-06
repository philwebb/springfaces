package org.springframework.springfaces.mvc.navigation;

import javax.faces.event.ActionEvent;

public interface NavigationContext {

	Object getHandler();

	String getFromAction();

	String getOutcome();

	boolean isPreEmptive();

	// FIXME DC. Always null for pre-emptive
	ActionEvent getActionEvent();
}
