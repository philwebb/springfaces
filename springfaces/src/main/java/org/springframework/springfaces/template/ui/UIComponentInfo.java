package org.springframework.springfaces.template.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Provides {@link ComponentInfo information} about the {@link EditableValueHolder} components that can be used when
 * writing facelet templates. Unless otherwise {@link #setFor(String) specified} information will be returned for each
 * child {@link EditableValueHolder} {@link UIComponent component}. This component is particularly useful when writing
 * <tt>ui:decorate</tt> templates that have <tt>ui:insert</tt> tags:
 * 
 * <pre>
 * &lt;ui:composition&gt;
 *   &lt;s:componentInfo var="info"&gt;
 *     &lt;h:outputLabel for="#{info.for}" value="#{info.label}#{info.required ? ' *' : ''}"/&gt;
 *     &lt;ui:insert/&gt;
 *   &lt;/s:componentInfo&gt;
 * &lt;/ui:composition&gt;
 * </pre>
 * 
 * @see ComponentInfo
 * @author Phillip Webb
 */
public class UIComponentInfo extends UIComponentBase {

	public static final String COMPONENT_FAMILY = "spring.faces.ComponentInfo";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	/**
	 * Return the client identifier of the component that the information is for. If not specified the children of this
	 * component will be used.
	 * @return the for component
	 */
	public String getFor() {
		return (String) getStateHelper().eval(PropertyKeys.forValue);
	}

	/**
	 * Set the client identifier of the component that the information is for. If not specified the children of this
	 * component will be used.
	 * @param forValue the for value
	 */
	public void setFor(String forValue) {
		getStateHelper().put(PropertyKeys.forValue, forValue);
	}

	/**
	 * Return the request-scope attribute under which the {@link ComponentInfo} will be exposed. This property is
	 * <b>not</b> enabled for value binding expressions.
	 * @return The variable name
	 */
	public String getVar() {
		String var = (String) getStateHelper().get(PropertyKeys.var);
		Assert.state(StringUtils.hasLength(var), "Please specify a 'var' for the ComponentInfo");
		return var;
	}

	/**
	 * Set the request-scope attribute under which the {@link ComponentInfo} will be exposed.
	 * @param var The new request-scope attribute name
	 */
	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(final FacesContext context) throws IOException {
		FacesUtils.doWithRequestScopeVariable(context, getVar(), getComponentInfo(context), new Callable<Object>() {
			public Object call() throws Exception {
				UIComponentInfo.this.doEncodeChildren(context);
				return null;
			}
		});
	}

	protected void doEncodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);
	}

	private ComponentInfo getComponentInfo(FacesContext context) {
		UIComponent sourceComponent = getForComponent();
		VisitContext visitContext = VisitContext.createVisitContext(context);
		EditableValueHoldersCollector collector = new EditableValueHoldersCollector();
		sourceComponent.visitTree(visitContext, collector);
		return new DefaultComponentInfo(context, collector.getEditableValueHolders());
	}

	private UIComponent getForComponent() {
		String forValue = getFor();
		if (StringUtils.hasLength(forValue)) {
			UIComponent compoent = findComponent(forValue);
			Assert.notNull(compoent, "Unable to find component info for '" + forValue + "'");
			return compoent;
		}
		return this;
	}

	private enum PropertyKeys {
		forValue {
			@Override
			public String toString() {
				return "for";
			}
		},
		var;
	}

	private static class EditableValueHoldersCollector implements VisitCallback {

		private List<UIComponent> editableValueHolders = new ArrayList<UIComponent>();

		public VisitResult visit(VisitContext context, UIComponent target) {
			if (target instanceof EditableValueHolder) {
				this.editableValueHolders.add(target);
				return VisitResult.REJECT;
			}
			return VisitResult.ACCEPT;
		}

		public List<UIComponent> getEditableValueHolders() {
			return this.editableValueHolders;
		}
	}

}
