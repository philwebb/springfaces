package org.springframework.springfaces.internal;

import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.event.ActionListenerWrapper;

/**
 * A JSF {@link ActionListener} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringActionListener extends ActionListenerWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private WrapperHandler<ActionListener> wrapperHandler;

	public SpringActionListener(ActionListener delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping ActionListener " + delegate.getClass() + " to provide integration with Spring");
		}
		this.wrapperHandler = WrapperHandler.get(ActionListener.class, delegate);
	}

	@Override
	public ActionListener getWrapped() {
		return wrapperHandler.getWrapped();
	}
}
