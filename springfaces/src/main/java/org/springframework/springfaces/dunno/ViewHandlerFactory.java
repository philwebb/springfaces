package org.springframework.springfaces.dunno;

import javax.faces.application.ViewHandler;

public interface ViewHandlerFactory {

	ViewHandler newViewHandler(ViewHandler delegate);

}
