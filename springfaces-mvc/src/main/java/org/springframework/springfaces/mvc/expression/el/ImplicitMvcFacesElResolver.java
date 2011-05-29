package org.springframework.springfaces.mvc.expression.el;

import javax.el.CompositeELResolver;

import org.springframework.springfaces.mvc.context.SpringFacesContext;

/**
 * Resolves "implicit" or well-known variables from SpringFaces MVC. The list of implicit flow variables consists of:
 * 
 * <pre>
 * controller
 * </pre>
 * 
 * @author Phillip Webb
 */
public class ImplicitMvcFacesElResolver extends CompositeELResolver {

	public ImplicitMvcFacesElResolver() {
		add(new SpringFacesContextElResolver());
	}

	/**
	 * Resolver for {@link SpringFacesContext} backed expressions.
	 */
	private static class SpringFacesContextElResolver extends BeanBackedElResolver {
		public SpringFacesContextElResolver() {
			map("handler", "controller");
		}

		protected Object getBean() {
			return SpringFacesContext.getCurrentInstance();
		}
	}
}
