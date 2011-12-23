package org.springframework.springfaces.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEventListener;

//FIXME look into generating meta-data like myfaces
//Use cases:
// Decorate components to include label
// Set value of outputText based on ID
// Change UIInput to UIOutput
// Highlight any field that contains an error
public class UIAspectGroup extends UIComponentBase implements ComponentSystemEventListener {

	public static final String COMPONENT_TYPE = "spring.faces.AspectGroup";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	private List<UIAspect> aspects = new ArrayList<UIAspect>();

	public UIAspectGroup() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	void addAspect(UIAspect aspect) {
		this.aspects.add(aspect);
	}

	void removeAspect(UIAspect aspect) {
		this.aspects.remove(aspect);
	}

	public List<UIAspect> getAllAspects() {
		List<UIAspect> allAspects = this.aspects;
		UIAspectGroup parentAspectGroup = getParentAspectGroup(getParent());
		if (parentAspectGroup != null) {
			allAspects = new ArrayList<UIAspect>(parentAspectGroup.getAllAspects());
			allAspects.addAll(this.aspects);
		}
		return Collections.unmodifiableList(allAspects);
	}

	private UIAspectGroup getParentAspectGroup(UIComponent component) {
		if (component == null || component instanceof UIAspectGroup) {
			return (UIAspectGroup) component;
		}
		return getParentAspectGroup(component.getParent());
	}

	public void applyAspects(final FacesContext context, AspectInvocation invocation) throws IOException {
		final Iterator<UIAspect> iterator = getAllAspects().iterator();
		new AspectInvocationWrapper(invocation) {
			@Override
			public void proceed() throws IOException {
				if (iterator.hasNext()) {
					UIAspect aspect = iterator.next();
					aspect.apply(context, this);
				} else {
					super.proceed();
				}
			}
		}.proceed();
	}
}
