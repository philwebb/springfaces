package org.springframework.springfaces.render;

import javax.faces.render.ResponseStateManager;

public interface ResponseStateManagerFactory {

	//FIXME make all factories inherit from base class
	//FIXME remove renderKitId and instead allow RSM to implement RenderKitIdAware

	ResponseStateManager newResponseStateManager(String renderKitId, ResponseStateManager delegate);

}
