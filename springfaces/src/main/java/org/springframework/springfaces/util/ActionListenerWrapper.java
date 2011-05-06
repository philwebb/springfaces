package org.springframework.springfaces.util;

import javax.faces.FacesWrapper;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * Provides a simple implementation of {@link ActionListener} that can be subclassed by developers wishing to provide
 * specialised behaviour to an existing {@link ActionListener instance} . The default implementation of all methods is
 * to call through to the wrapped {@link ActionListener}.
 * 
 * @author Phillip Webb
 */
public abstract class ActionListenerWrapper implements ActionListener, FacesWrapper<ActionListener> {

	public abstract ActionListener getWrapped();

	public void processAction(ActionEvent event) throws AbortProcessingException {
		getWrapped().processAction(event);
	}
}
