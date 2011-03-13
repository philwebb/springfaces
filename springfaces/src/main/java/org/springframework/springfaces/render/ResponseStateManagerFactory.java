package org.springframework.springfaces.render;

import javax.faces.render.ResponseStateManager;

public interface ResponseStateManagerFactory {

	//FIXME make all factories inherit from base class

	ResponseStateManager newResponseStateManager(ResponseStateManager delegate);

}
