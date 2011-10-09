package org.springframework.springfaces.message.ui;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.context.MessageSource;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.message.ui.MessageSourceMap.LocaleProvider;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Exposes a Spring {@link MessageSource} for use with JSF pages.
 * 
 * @author Phillip Webb
 */
public class UIMessageSource extends UIComponentBase {

	// FIXME
	// codePrefix : a comma list of prefixes applied to the source, defaults to page.<UIViewRoot>

	// "customer.jspx"
	//
	// <s:messageSource var="messages"/>

	// <h:outputText value="#{messages.name}"/>
	// <s:messageSource var="messages" codePrefix="page.@, page.common"/>
	//
	// page.customer.name=Name:
	// page.customer.age=Age:

	public static final String COMPONENT_FAMILY = "spring.faces.MessageSource";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getVar() {
		return (String) getStateHelper().get(PropertyKeys.var);
	}

	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
	}

	public MessageSource getSource() {
		return (MessageSource) getStateHelper().get(PropertyKeys.source);
	}

	public void setSource(MessageSource source) {
		getStateHelper().put(PropertyKeys.source, source);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		Assert.state(StringUtils.hasLength(getVar()), "No 'var' attibute specified for UIMessageSource component");
		MessageSource messageSource = findMessageSource(context);
		String[] prefixCodes = getPrefixCodes();
		LocaleProvider localeProvider = new FacesLocaleProvider(context);
		MessageSourceMap messageSourceMap = new MessageSourceMap(messageSource, prefixCodes, localeProvider);
		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		Object previous = requestMap.put(getVar(), messageSourceMap);
		if (previous != null) {
			// FIXME log a warning
		}
		System.out.println(context.getViewRoot().getViewId());
	}

	private MessageSource findMessageSource(FacesContext context) {
		Assert.notNull(context, "Context must not be null");
		MessageSource source = getSource();
		if (source == null) {
			ExternalContext externalContext = context.getExternalContext();
			if (SpringFacesIntegration.isInstalled(externalContext)) {
				source = SpringFacesIntegration.getCurrentInstance(externalContext).getApplicationContext();
			}
		}
		Assert.state(source != null, "Unable to find MessageSource, ensure that SpringFaces intergation "
				+ "is enabled or set the 'source' attribute");
		return source;
	}

	private String[] getPrefixCodes() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	private enum PropertyKeys {
		source, var
	}

	private static class FacesLocaleProvider implements LocaleProvider {

		private FacesContext context;

		public FacesLocaleProvider(FacesContext context) {
			this.context = context;
		}

		public Locale getLocale() {
			return FacesUtils.getLocale(context);
		}

	}

}
