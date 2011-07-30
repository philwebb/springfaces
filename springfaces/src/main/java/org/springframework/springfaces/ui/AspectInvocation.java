package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;

public interface AspectInvocation {

	UIComponent getComponent();

	void proceed() throws IOException;
}
