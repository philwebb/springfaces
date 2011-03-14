package org.springframework.springfaces.mvc;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class TestApplication extends ApplicationWrapper {

	private Application delegate;

	public TestApplication(Application delegate) {
		this.delegate = delegate;
	}

	@Override
	public Application getWrapped() {
		return delegate;
	}

	@Override
	public UIComponent createComponent(String componentType) throws FacesException {
		System.out.println("Create component " + componentType);
		return super.createComponent(componentType);
	}

	@Override
	public UIComponent createComponent(FacesContext context, String componentType, String rendererType) {
		System.out.println("Create component");
		return super.createComponent(context, componentType, rendererType);
	}

	@Override
	public Converter createConverter(String converterId) {
		System.out.println("created converter " + converterId);
		return super.createConverter(converterId);
	}
}
