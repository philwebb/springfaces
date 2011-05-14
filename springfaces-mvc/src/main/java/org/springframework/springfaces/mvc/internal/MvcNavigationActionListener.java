package org.springframework.springfaces.mvc.internal;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * Action listener that stores the {@link ActionEvent} so that the {@link MvcNavigationHandler} can obtain it later.
 * 
 * @author Phillip Webb
 */
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

	/**
	 * Returns the last {@link ActionEvent} that occurred.
	 * @param context The faces context
	 * @return The action event or <tt>null</tt>
	 */
	public static ActionEvent getLastActionEvent(FacesContext context) {
		return (ActionEvent) context.getAttributes().get(KEY);
	}
}
