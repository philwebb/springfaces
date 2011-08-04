package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;

/**
 * @author Phillip Webb
 */
public interface AspectInvocation {

	// FIXME DC

	UIComponent getComponent();

	void proceed() throws IOException;
}
