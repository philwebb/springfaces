package org.springframework.springfaces.application;

import javax.faces.application.ViewHandler;

public interface ViewHandlerFactory {

	ViewHandler newViewHandler(ViewHandler delegate);

}
