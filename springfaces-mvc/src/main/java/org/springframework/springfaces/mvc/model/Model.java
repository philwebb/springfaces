package org.springframework.springfaces.mvc.model;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;

import org.springframework.util.Assert;

public class Model extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	private static final String KEY = Model.class.getName();

	public Model(Map<String, Object> model) {
		super(model);
	}

	public static void put(UIViewRoot viewRoot, Map<String, Object> model) {
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		Assert.notNull(model, "Model must not be null");
		viewRoot.getViewMap().put(KEY, new Model(model));
	}

	public static Model get(UIViewRoot viewRoot) {
		if (viewRoot == null) {
			return null;
		}
		return (Model) viewRoot.getViewMap().get(KEY);
	}
}
