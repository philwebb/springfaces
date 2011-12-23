package org.springframework.springfaces.mvc.expression.el;

import javax.el.CompositeELResolver;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.expression.el.AbstractELResolver;
import org.springframework.springfaces.expression.el.BeanBackedELResolver;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * Resolves "implicit" or well-known variables from SpringFaces MVC. The list of implicit flow variables consists of:
 * 
 * <pre>
 * handler
 * controller
 * model
 * </pre>
 * 
 * @author Phillip Webb
 */
public class ImplicitSpringFacesELResolver extends CompositeELResolver {

	public ImplicitSpringFacesELResolver() {
		add(new SpringFacesContextELResolver());
		add(new ModelELResolver());
	}

	/**
	 * Resolver for {@link SpringFacesContext} backed expressions.
	 */
	private static class SpringFacesContextELResolver extends BeanBackedELResolver {
		public SpringFacesContextELResolver() {
			map("handler");
			map("controller");
		}

		@Override
		protected Object getBean() {
			return SpringFacesContext.getCurrentInstance();
		}
	}

	private static class ModelELResolver extends AbstractELResolver {
		@Override
		protected Object get(String property) {
			if ("model".equals(property) && FacesContext.getCurrentInstance() != null) {
				FacesContext context = FacesContext.getCurrentInstance();
				return SpringFacesModelHolder.getModel(context.getViewRoot());
			}
			return null;
		}

	}
}
