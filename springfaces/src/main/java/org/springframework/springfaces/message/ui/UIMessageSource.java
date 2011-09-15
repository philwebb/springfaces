package org.springframework.springfaces.message.ui;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public class UIMessageSource extends UIComponentBase {

	// FIXME
	// source : the MessageSource, if not specified defaults to the WAC
	// var : the name of that the MSM is exposed as (defaults to msg)
	// prefix : a code prefix applied to the source
	// includeViewId
	// hierarchical

	public static final String COMPONENT_FAMILY = "spring.faces.MessageSource";

	private static final String DEFAULT_VAR = "msg";

	public String getVar() {
		String var = (String) getStateHelper().get(PropertyKeys.var);
		return var == null ? DEFAULT_VAR : var;
	}

	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);
	}

	private enum PropertyKeys {
		source, var
	}

}
