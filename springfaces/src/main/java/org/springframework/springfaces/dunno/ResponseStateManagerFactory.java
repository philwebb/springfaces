package org.springframework.springfaces.dunno;

import javax.faces.render.ResponseStateManager;

public interface ResponseStateManagerFactory {

	ResponseStateManager newResponseStateManager(String renderKitId, ResponseStateManager delegate);

}
