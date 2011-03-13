package org.springframework.springfaces.render;

import javax.faces.render.ResponseStateManager;

public interface ResponseStateManagerFactory {

	ResponseStateManager newResponseStateManager(String renderKitId, ResponseStateManager delegate);

}
