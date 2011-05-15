package org.springframework.springfaces.mvc.internal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;

import org.springframework.util.Assert;

public class ModelHolder extends HashMap<String, Object> {

	private static final long serialVersionUID = -3787338680511787303L;

	private static final String KEY = ModelHolder.class.getName();

	public ModelHolder(Map<String, Object> model) {
		super(model);
	}

	public static void put(UIViewRoot viewRoot, Map<String, Object> model) {
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		Assert.notNull(model, "Model must not be null");
		viewRoot.getViewMap().put(KEY, new ModelHolder(model));
	}

	public static ModelHolder get(UIViewRoot viewRoot) {
		if (viewRoot == null) {
			return null;
		}
		return (ModelHolder) viewRoot.getViewMap().get(KEY);
	}
}
