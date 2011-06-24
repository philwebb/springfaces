package org.springframework.springfaces.mvc.navigation.annotation;

import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.support.RequestResponseBodyMethodProcessor;

public class NavigationResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

	public NavigationResponseBodyMethodProcessor(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return false;
	}

	@Override
	protected <T extends Object> void writeWithMessageConverters(T returnValue, MethodParameter returnType,
			NativeWebRequest webRequest) throws java.io.IOException, HttpMediaTypeNotAcceptableException {
		super.writeWithMessageConverters(returnValue, returnType, webRequest);
		FacesContext.getCurrentInstance().responseComplete();
	};
}
