package org.springframework.springfaces.mvc.model;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;

import org.springframework.springfaces.mvc.expression.el.SpringFacesModelELResolver;
import org.springframework.util.Assert;

/**
 * @see SpringFacesModelELResolver
 * @author Phillip Webb
 */
public class SpringFacesModel extends HashMap<String, Object> {

	// FIXME DC

	private static final long serialVersionUID = 1L;

	private static final String KEY = SpringFacesModel.class.getName();

	public SpringFacesModel(Map<String, Object> model) {
		super(model);
	}

	public static void put(UIViewRoot viewRoot, Map<String, Object> model) {
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		Assert.notNull(model, "Model must not be null");
		viewRoot.getViewMap().put(KEY, new SpringFacesModel(model));
	}

	public static SpringFacesModel get(UIViewRoot viewRoot) {
		if (viewRoot == null) {
			return null;
		}
		return (SpringFacesModel) viewRoot.getViewMap().get(KEY);
	}
}
