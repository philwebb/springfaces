package org.springframework.springfaces.mvc.navigation.annotation.support;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class NavigationMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	public boolean supportsReturnType(MethodParameter returnType) {
		return true;
	}

	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		mavContainer.setView(returnValue);
	}
}