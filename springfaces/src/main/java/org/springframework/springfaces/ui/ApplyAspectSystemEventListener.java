package org.springframework.springfaces.ui;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class ApplyAspectSystemEventListener implements SystemEventListener {

	public boolean isListenerForSource(Object source) {
		return true;
	}

	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PostAddToViewEvent) {
			processPostAddToViewEvent((PostAddToViewEvent) event);
		}
	}

	private void processPostAddToViewEvent(PostAddToViewEvent event) {
	}
}
