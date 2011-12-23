package org.springframework.springfaces.mvc.internal;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PreRenderComponentEvent;

import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.util.Assert;
import org.springframework.web.servlet.View;

/**
 * Data holder that provides access to a navigation destination and a model.
 * 
 * @author Phillip Webb
 */
class DestinationAndModel {

	private NavigationOutcome navigationOutcome;

	private UIComponent component;

	/**
	 * Create a new DestinationAndModel
	 * @param navigationOutcome the navigation outcome
	 * @param preRenderComponentEvent an optional pre-render component event
	 */
	public DestinationAndModel(NavigationOutcome navigationOutcome, PreRenderComponentEvent preRenderComponentEvent) {
		Assert.notNull(navigationOutcome, "NavigationOutcome must not be null");
		this.navigationOutcome = navigationOutcome;
		if (preRenderComponentEvent != null) {
			this.component = preRenderComponentEvent.getComponent();
		}
	}

	/**
	 * Create a new DestinationAndModel
	 * @param navigationOutcome the navigation outcome
	 * @param actionEvent an optional navigation event
	 */
	public DestinationAndModel(NavigationOutcome navigationOutcome, ActionEvent actionEvent) {
		Assert.notNull(navigationOutcome, "NavigationOutcome must not be null");
		this.navigationOutcome = navigationOutcome;
		if (actionEvent != null) {
			this.component = actionEvent.getComponent();
		}
	}

	/**
	 * Returns the UIComponent that relates to the destination and model.
	 * @return The component or <tt>null</tt>.
	 */
	protected UIComponent getComponent() {
		return this.component;
	}

	/**
	 * Returns the destination of the next view to render. The destination can be a MVC {@link View} or an object that
	 * can be resolved by a {@link DestinationViewResolver}.
	 * @return the destination
	 */
	public Object getDestination() {
		return this.navigationOutcome.getDestination();
	}

	/**
	 * Returns the model that should be passed to the destination when it is rendered. The model returned here will
	 * include {@link NavigationOutcome#getImplicitModel() implicit} entries as well as &lt;f:param$gt;s defined in the
	 * page mark-up.
	 * @param context the faces context
	 * @param parameters parameters identified by JSF that should be included in the model.
	 * @param resolvedViewModel the model obtained when the destination was {@link DestinationViewResolver resolved}
	 * (can be <tt>null</tt>)
	 * @return the model The final model
	 */
	public Map<String, Object> getModel(FacesContext context, Map<String, List<String>> parameters,
			Map<String, Object> resolvedViewModel) {
		ModelBuilder modelBuilder = newModelBuilder(context);
		modelBuilder.addFromComponent(getComponent());
		modelBuilder.add(this.navigationOutcome.getImplicitModel(), true);
		modelBuilder.addFromParameterList(parameters);
		modelBuilder.add(resolvedViewModel, false);
		return modelBuilder.getModel();
	}

	/**
	 * Factory method used to create a {@link ModelBuilder}.
	 * @param context The faces context
	 * @return a model builder
	 */
	protected ModelBuilder newModelBuilder(FacesContext context) {
		return new ModelBuilder(context);
	}
}
