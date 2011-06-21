package org.springframework.springfaces.mvc.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.springfaces.mvc.expression.el.SpringFacesModelELResolver;
import org.springframework.util.Assert;

/**
 * The Model that relates to a Spring Faces MVC request.
 * 
 * @see SpringFacesModelELResolver
 * @author Phillip Webb
 */
public class SpringFacesModel extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new empty Spring Faces Model.
	 */
	public SpringFacesModel() {
	}

	/**
	 * Create a new model containing elements from the specified source.
	 * @param source a map containing the initial model
	 */
	public SpringFacesModel(Map<String, ?> source) {
		Assert.notNull(source, "Source must not be null");
		putAll(source);
	}
}
