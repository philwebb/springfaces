package org.springframework.springfaces.mvc.expression.el;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.expression.el.AbstractELResolver;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * Unified EL {@link ELResolver} that exposes values from the Spring Faces MVC model.
 * 
 * @see SpringFacesModel
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelELResolver extends AbstractELResolver {

	@Override
	protected Object get(String property) {
		FacesContext context = FacesContext.getCurrentInstance();
		SpringFacesModel model = (context == null ? null : SpringFacesModelHolder.getModel(context.getViewRoot()));
		if (model != null) {
			return model.get(property);
		}
		return null;
	}
}
