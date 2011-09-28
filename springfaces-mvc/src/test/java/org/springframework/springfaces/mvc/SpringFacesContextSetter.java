package org.springframework.springfaces.mvc;

import org.springframework.springfaces.mvc.context.SpringFacesContext;

/**
 * Class to allow tests to set the SpringFacesContext instance.
 * 
 * @author Phillip Webb
 */
public abstract class SpringFacesContextSetter extends SpringFacesContext {

	public static void setCurrentInstance(SpringFacesContext context) {
		SpringFacesContext.setCurrentInstance(context);
	}

}
