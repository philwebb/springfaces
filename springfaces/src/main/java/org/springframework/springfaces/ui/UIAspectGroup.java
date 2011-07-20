package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;

//FIXME look into generatiing like myfaces

public class UIAspectGroup extends UIComponentBase {

	public static final String COMPONENT_TYPE = "spring.faces.AspectGroup";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public boolean visitTree(VisitContext context, VisitCallback callback) {
		// FIXME see UIData
		// Like encode
		// Set the component before each call
		return super.visitTree(context, callback);
	}

	@Override
	public String getClientId(FacesContext context) {
		// FIXME include the active component ID
		return super.getClientId(context);
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		FacesAdvice advice = createAdvice();
		encodeChildrenWithAdice(context, this, advice);
	}

	private FacesAdvice createAdvice() {
		return new FacesAdvice() {
			public void before(FacesContext context, UIComponent component) throws IOException {
			}

			public void after(FacesContext context, UIComponent component) throws IOException {
			}
		};
	}

	public void encodeWithAdvice(FacesContext context, UIComponent component, FacesAdvice advice) throws IOException {
		if (component.isRendered()) {
			advice.before(context, component);
			component.encodeBegin(context);
			if (component.getRendersChildren()) {
				component.encodeChildren(context);
			} else {
				encodeChildrenWithAdice(context, component, advice);
			}
			component.encodeEnd(context);
			advice.after(context, component);
		}
	}

	private void encodeChildrenWithAdice(FacesContext context, UIComponent component, FacesAdvice advice)
			throws IOException {
		if (component.getChildCount() > 0) {
			for (UIComponent child : component.getChildren()) {
				encodeWithAdvice(context, child, advice);
			}
		}
	}

	private static interface FacesAdvice {
		void before(FacesContext context, UIComponent component) throws IOException;

		void after(FacesContext context, UIComponent component) throws IOException;
	}

}
