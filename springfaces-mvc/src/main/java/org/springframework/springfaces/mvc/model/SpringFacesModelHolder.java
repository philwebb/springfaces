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
 * 
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

	/**
	 * Creates a new {@link SpringFacesModelHolder} with the specified model.
	 * @param model the model that should be contained in the holder component (can be <tt>null</tt>)
	 * @see #getModel()
	 */
	public SpringFacesModelHolder(Map<String, ?> model) {
		this.model = model == null ? null : new SpringFacesModel(model);
	}

	public String getId() {
		return COMPONENT_ID;
	}

	public void setId(String id) {
		// Do nothing so as to ensure the id never gets overwritten.
		return;
	}

	public String getClientId(FacesContext context) {
		return COMPONENT_ID;
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public Renderer getRenderer() {
		// this component is not rendered
		return null;
	}

	public boolean isTransient() {
		return transientValue;
	}

	public void setTransient(boolean transientValue) {
		this.transientValue = transientValue;
	}

	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		model = (SpringFacesModel) values[0];
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[1];
		values[0] = model;
		return values;
	}

	public SpringFacesModel getModel() {
		return model;
	}

	/**
	 * Attach the holder component to the view root and set the optional model.
	 * @param context the faces context
	 * @param viewRoot the view root
	 * @param model an optional model
	 */
	public static void attach(FacesContext context, UIViewRoot viewRoot, Map<String, ?> model) {
		Assert.notNull(context, "FacesContext must not be null");
		Assert.notNull(viewRoot, "ViewRoot must not be null");
		viewRoot.getChildren().add(new SpringFacesModelHolder(model));
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
