package org.springframework.springfaces.mvc.expression.el;

import javax.el.CompositeELResolver;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.model.Model;

/**
 * Resolves "implicit" or well-known variables from SpringFaces MVC. The list of implicit flow variables consists of:
 * 
 * <pre>
 * controller
 * model
 * </pre>
 * 
 * @author Phillip Webb
 */
public class ImplicitMvcFacesELResolver extends CompositeELResolver {

	public ImplicitMvcFacesELResolver() {
		add(new SpringFacesContextELResolver());
		add(new ModelELResolver());
	}

	/**
	 * Resolver for {@link SpringFacesContext} backed expressions.
	 */
	private static class SpringFacesContextELResolver extends BeanBackedELResolver {
		public SpringFacesContextELResolver() {
			map("handler", "controller");
		}

		protected Object getBean() {
			return SpringFacesContext.getCurrentInstance();
		}
	}

	private static class ModelELResolver extends AbstractELResolver {
		@Override
		protected Object get(String property) {
			if ("model".equals(property) && FacesContext.getCurrentInstance() != null) {
				return Model.get(FacesContext.getCurrentInstance().getViewRoot());
			}
			return null;
		}

	}
}
