package org.springframework.springfaces.mvc.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Conventions;
import org.springframework.util.StringUtils;

/**
 * Utility class that can be used to combine several sources to build a complete model. Elements can be added to the
 * model using the various <tt>add</tt> methods. When trying to add an item with a key that is already contained in the
 * model the existing value is retained. The add methods should be called in order of precedence, with the highest
 * importance being called first.
 * 
 * 
 * @see #add(Map, boolean)
 * @see #addFromComponent(UIComponent)
 * @see #addFromParamterList(Map)
 * 
 * @author Phillip Webb
 */
public class ModelBuilder {

	private Log logger = LogFactory.getLog(ModelBuilder.class);

	private FacesContext context;

	private Map<String, Object> model = new HashMap<String, Object>();

	/**
	 * Create a new ModelBuilder.
	 * @param context
	 */
	public ModelBuilder(FacesContext context) {
		this.context = context;
	}

	/**
	 * Add model elements by inspecting all {@link UIParameter} children of the specified component. Child parameters
	 * that do not specify a name will have one generated using Spring {@link Conventions#getVariableName conventions}.
	 * If a parameter references an existing MVC {@link ModelHolder model} then the complete model will be added.
	 * @param component the component to inspect or <tt>null</tt>
	 */
	public void addFromComponent(UIComponent component) {
		if (component != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exposing UIParameter children of component " + component.getClientId(context)
						+ " to MVC model");
			}
			for (UIComponent child : component.getChildren()) {
				if (child instanceof UIParameter) {
					UIParameter parameter = (UIParameter) child;
					addUIParam(parameter);
				}
			}
		}
	}

	/**
	 * Adds a single {@link UIParameter} to the model.
	 * @param parameter the parameter to add
	 */
	private void addUIParam(UIParameter parameter) {
		String source = parameter.getClientId(context)
				+ (parameter.getName() == null ? "" : " ('" + parameter.getName() + "')");
		if (parameter.isDisable()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping disabled parameter " + source);
			}
			return;
		}
		addIfNotInModel(source, parameter.getName(), parameter.getValue(), false, true);
	}

	/**
	 * Add model elements from the specified map. When <tt>resolveExpressions</tt> is <tt>true</tt> the map may contain
	 * String EL expressions that will be resolved as the model is built.
	 * @param map a map of items to add to the model or <tt>null</tt>
	 * @param resolveExpressions if the EL expression from <tt>String<tt> values in the map should be resolved.
	 */
	public void add(Map<String, Object> map, boolean resolveExpressions) {
		if (map != null) {
			for (Map.Entry<String, Object> modelEntry : map.entrySet()) {
				addIfNotInModel(modelEntry.getKey(), modelEntry.getKey(), modelEntry.getValue(), resolveExpressions,
						false);
			}
		}
	}

	/**
	 * Add model elements from a JSF parameters map. Only entries with a single parameter will be added to the model.
	 * Parameters may contain String EL expressions that will be resolved as the model is built. NOTE: JSF Parameters
	 * are often constructed from {@link UIParameter}s. Whenever possible call {@link #addFromComponent(UIComponent)} to
	 * add {@link UIParameter}s before calling this method.
	 * @param parameters the parameters to add or <tt>null</tt>
	 */
	public void addFromParamterList(Map<String, List<String>> parameters) {
		if (parameters != null) {
			for (Map.Entry<String, List<String>> parameter : parameters.entrySet()) {
				if (parameter.getValue().size() == 1) {
					addIfNotInModel(parameter.getKey(), parameter.getKey(), parameter.getValue().get(0), true, false);
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Unable to expose multi-value parameter '" + parameter.getKey()
								+ "' to bookmark model");
					}
				}
			}
		}
	}

	/**
	 * Adds the specified key/value pair to the model as long as the model does not already contain the key.
	 * @param source a textual description of the source of the item that can be used for logging
	 * @param key the key to add to the model or <tt>null</tt> if the key should be generated from the value
	 * @param value the value to add to the model. If the value is not specified then the model remains unchanged
	 * @param resolveExpressions determines if values can contain <tt>String</tt> EL expression that should be resolved
	 * @param expandModelHolder determines if values containing {@link ModelHolder} objects should have each member of
	 * the holder added to the model as a separate item
	 */
	private void addIfNotInModel(String source, String key, Object value, boolean resolveExpressions,
			boolean expandModelHolder) {
		if (value == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping parameter " + source + " due to null value");
			}
			return;
		}
		if (key == null) {
			key = Conventions.getVariableName(value);
		}
		if (model.containsKey(key)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping parameter " + source + " due to exsting value in model");
			}
			return;
		}
		if (resolveExpressions) {
			value = resolveExpressionIfNecessary(value);
		}
		if (value instanceof ModelHolder && expandModelHolder) {
			ModelHolder modelHolder = (ModelHolder) value;
			for (Map.Entry<String, Object> modelEntry : modelHolder.entrySet()) {
				addIfNotInModel(source, modelEntry.getKey(), modelEntry.getValue(), false, false);
			}
		} else {
			model.put(key, value);
		}
	}

	/**
	 * Resolve any <tt>String</tt> EL expressions from the value.
	 * @param value the value to resolve
	 * @return a resolved EL expression or the value unchanged
	 */
	private Object resolveExpressionIfNecessary(Object value) {
		if (isExpression(value)) {
			return context.getApplication().evaluateExpressionGet(context, value.toString(), Object.class);
		}
		return value;
	}

	/**
	 * Determine if an object contains an expression.
	 * @param value the value to check
	 * @return <tt>true</tt> if the value contains an EL expression
	 */
	private boolean isExpression(Object value) {
		if (value instanceof String && StringUtils.hasLength((String) value)) {
			String expressionString = (String) value;
			int start = expressionString.indexOf("#{");
			int end = expressionString.indexOf('}');
			return (start != -1) && (start < end);
		}
		return false;
	}

	public Map<String, Object> getModel() {
		return model;
	}
}
