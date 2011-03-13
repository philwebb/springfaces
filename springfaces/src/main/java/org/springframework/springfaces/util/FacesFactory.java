package org.springframework.springfaces.util;

import javax.faces.FactoryFinder;

public class FacesFactory {

	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> factoryClass) {
		return (T) FactoryFinder.getFactory(factoryClass.getName());
		//FIXME check return type?
	}

}
