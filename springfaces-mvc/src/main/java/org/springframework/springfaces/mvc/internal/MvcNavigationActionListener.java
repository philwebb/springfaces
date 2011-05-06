package org.springframework.springfaces.mvc.internal;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

public class MvcNavigationActionListener implements ActionListener {

	private static final String KEY = MvcNavigationActionListener.class.getName();

	private ActionListener delegate;

	public MvcNavigationActionListener(ActionListener delegate) {
		this.delegate = delegate;
	}

	public void processAction(ActionEvent event) throws AbortProcessingException {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getAttributes().put(KEY, event);
		delegate.processAction(event);
	}

	public static ActionEvent get(FacesContext context) {
		return (ActionEvent) context.getAttributes().get(KEY);
	}
}
