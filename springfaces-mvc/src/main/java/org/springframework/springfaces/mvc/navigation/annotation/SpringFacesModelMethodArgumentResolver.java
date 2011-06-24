package org.springframework.springfaces.mvc.navigation.annotation;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SpringFacesModelMethodArgumentResolver implements HandlerMethodArgumentResolver {

	public boolean supportsParameter(MethodParameter parameter) {
		SpringFacesModel model = getSpringFacesModel();
		if (model == null) {
			return false;
		}
		return ((parameter.getParameterType().isInstance(model)) || (findModelValue(model, parameter) != null));
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		SpringFacesModel model = getSpringFacesModel();
		Assert.state(model != null, "Unable to obtain SpringFacesModel");
		if (parameter.getParameterType().isInstance(model)) {
			return model;
		}
		return findModelValue(model, parameter);
	}

	private Object findModelValue(SpringFacesModel model, MethodParameter parameter) {
		if (BeanUtils.isSimpleProperty(parameter.getParameterType())) {
			return null;
		}
		Object value = null;
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			if (parameter.getParameterType().isInstance(entry.getValue())) {
				if (value != null) {
					return null;
				}
				value = entry.getValue();
			}
		}
		return value;
	}

	private SpringFacesModel getSpringFacesModel() {
		FacesContext context = FacesContext.getCurrentInstance();
		return SpringFacesModelHolder.getModel(context == null ? null : context.getViewRoot());
	}
}
