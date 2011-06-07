package org.springframework.springfaces.mvc.model;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;

import org.springframework.springfaces.mvc.expression.el.SpringFacesModelELResolver;
import org.springframework.util.Assert;

/**
 * Holder for the Model that relates to a Spring Faces MVC request.
 * 
 * @see SpringFacesModelELResolver
 * @author Phillip Webb
 */
public class SpringFacesModel extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	private static final String KEY = SpringFacesModel.class.getName();

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

	/**
	 * Save the specified {@link SpringFacesModel} in the view scope of a {@link UIViewRoot}.
	 * @param viewRoot the view root used to obtain a view scope
	 * @param model the model to save
	 * @see #loadFromViewScope
	 */
	public static void saveInViewScope(UIViewRoot viewRoot, SpringFacesModel model) {
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		Assert.notNull(model, "Model must not be null");
		viewRoot.getViewMap().put(KEY, model);
	}

	/**
	 * Load a {@link SpringFacesModel} from the view scope of a {@link UIViewRoot}.
	 * @param viewRoot the view root used to obtain a view scope or <tt>null</tt>
	 * @return a {@link SpringFacesModel} or <tt>null</tt> if view scope does not contain a spring faces model.
	 * @see #saveInViewScope
	 */
	public static SpringFacesModel loadFromViewScope(UIViewRoot viewRoot) {
		if (viewRoot == null) {
			return null;
		}
		return (SpringFacesModel) viewRoot.getViewMap().get(KEY);
	}
}
