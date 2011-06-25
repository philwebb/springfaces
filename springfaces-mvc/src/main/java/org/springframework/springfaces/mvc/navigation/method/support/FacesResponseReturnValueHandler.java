package org.springframework.springfaces.mvc.navigation.method.support;

import javax.faces.context.FacesContext;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FacesResponseReturnValueHandler implements HandlerMethodReturnValueHandler {

	private HandlerMethodReturnValueHandler resolver;

	public FacesResponseReturnValueHandler(HandlerMethodReturnValueHandler resolver) {
		Assert.isInstanceOf(HandlerMethodReturnValueHandler.class, resolver);
		this.resolver = resolver;
	}

	public boolean supportsReturnType(MethodParameter returnType) {
		return resolver.supportsReturnType(returnType);
	}

	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		resolver.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		FacesContext.getCurrentInstance().responseComplete();
	}

}
