package org.springframework.springfaces;

import javax.faces.context.FacesContext;

/**
 * Class to allow tests to set the FacesContext instance.
 * 
 * @author Phillip Webb
 */
public abstract class FacesContextSetter extends FacesContext {
	public static void setCurrentInstance(FacesContext facesContext) {
		FacesContext.setCurrentInstance(facesContext);
	}
}