package org.springframework.springfaces.mvc.internal;

import java.util.Map;

import javax.faces.component.UIViewRoot;

import org.springframework.util.Assert;

public class UIViewRootModelStore {

	private static final String KEY = UIViewRootModelStore.class.getName();

	private UIViewRoot viewRoot;

	public UIViewRootModelStore(UIViewRoot viewRoot) {
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		this.viewRoot = viewRoot;
	}

	public void storeModel(Map<String, Object> model) {
		Assert.notNull(model, "Model must not be null");
		viewRoot.getViewMap().put(KEY, model);
	}

}
