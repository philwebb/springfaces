/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.mvc.model;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.springframework.util.Assert;

/**
 * A JSF component that holds a reference to the {@link SpringFacesModel}.
 * @author Phillip Webb
 */
public class SpringFacesModelHolder extends UIComponentBase {

	private static final String COMPONENT_FAMILY = "javax.faces.Parameter";

	/**
	 * Immutable id of the flow execution key component for easier lookup later.
	 */
	public static final String COMPONENT_ID = "SpringFacesModelHolder";

	private boolean transientValue;

	private SpringFacesModel model;

	public SpringFacesModelHolder() {
		this(null);
	}

	/**
	 * Creates a new {@link SpringFacesModelHolder} with the specified model.
	 * @param model the model that should be contained in the holder component (can be <tt>null</tt>)
	 * @see #getModel()
	 */
	public SpringFacesModelHolder(Map<String, ?> model) {
		this.model = model == null ? null : new SpringFacesModel(model);
	}

	@Override
	public String getId() {
		return COMPONENT_ID;
	}

	@Override
	public void setId(String id) {
		// Do nothing so as to ensure the id never gets overwritten.
		return;
	}

	@Override
	public String getClientId(FacesContext context) {
		return COMPONENT_ID;
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public Renderer getRenderer() {
		// this component is not rendered
		return null;
	}

	@Override
	public boolean isTransient() {
		return this.transientValue;
	}

	@Override
	public void setTransient(boolean transientValue) {
		this.transientValue = transientValue;
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		this.model = (SpringFacesModel) values[0];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[1];
		values[0] = this.model;
		return values;
	}

	public SpringFacesModel getModel() {
		return this.model;
	}

	/**
	 * Attach the holder component to the view root and set the optional model.
	 * @param context the faces context
	 * @param viewRoot the view root
	 * @param model an optional model
	 * @return the attached model
	 */
	public static SpringFacesModel attach(FacesContext context, UIViewRoot viewRoot, Map<String, ?> model) {
		Assert.notNull(context, "FacesContext must not be null");
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		SpringFacesModelHolder holder = new SpringFacesModelHolder(model);
		viewRoot.getChildren().add(holder);
		return holder.getModel();
	}

	/**
	 * Utility method that can be used to obtain the {@link SpringFacesModel} from the holder component contained in the
	 * specified viewRoot.
	 * @param viewRoot the viewRoot (can be null)
	 * @return The model or <tt>null</tt> if there is no {@link SpringFacesModelHolder} or the holder does not contain a
	 * model.
	 */
	public static SpringFacesModel getModel(UIViewRoot viewRoot) {
		UIComponent component = (viewRoot == null ? null : viewRoot.findComponent(COMPONENT_ID));
		return (component == null ? null : ((SpringFacesModelHolder) component).getModel());
	}
}
