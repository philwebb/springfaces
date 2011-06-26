package org.springframework.springfaces.mvc.method.support;

import javax.faces.context.FacesContext;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Decorator class that wraps {@link HandlerMethodReturnValueHandler}s to ensure that the current {@link FacesContext}
 * is marked as {@link FacesContext#responseComplete() responseComplete()} after the return value has been
 * {@link #handleReturnValue handled}.
 * 
 * @author Phillip Webb
 */
public class FacesResponseReturnValueHandler implements HandlerMethodReturnValueHandler {

	private HandlerMethodReturnValueHandler handler;

	/**
	 * Creates a new {@link FacesResponseReturnValueHandler} what wraps the specified handler to ensure that the
	 * {@link FacesContext#responseComplete()} is called after the return value has been handled.
	 * @param handler the delegate handler
	 */
	public FacesResponseReturnValueHandler(HandlerMethodReturnValueHandler handler) {
		Assert.notNull(handler, "Handler must not be null");
		this.handler = handler;
	}

	public boolean supportsReturnType(MethodParameter returnType) {
		return handler.supportsReturnType(returnType);
	}

	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		FacesContext.getCurrentInstance().responseComplete();
	}
}
