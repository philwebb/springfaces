package org.springframework.springfaces.servlet.view;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.context.SpringFacesContext;
import org.springframework.springfaces.servlet.SpringFacesServletContext;
import org.springframework.springfaces.view.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class FacesView extends AbstractUrlBasedView implements View {

	//FIXME include the postback URL as an option, default is back to self?

	public FacesView() {
		super();
	}

	public FacesView(String url) {
		super(url);
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//FIXME check type
		((SpringFacesServletContext) SpringFacesContext.getCurrentInstance()).render(this);
	}

	@Override
	public boolean checkResource(Locale locale) throws Exception {
		//FIXME check if the resource exists
		return super.checkResource(locale);
	}
}
