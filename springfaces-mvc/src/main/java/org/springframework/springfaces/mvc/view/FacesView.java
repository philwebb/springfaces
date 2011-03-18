package org.springframework.springfaces.mvc.view;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class FacesView extends AbstractUrlBasedView {

	public FacesView() {
		super();
	}

	public FacesView(String url) {
		super(url);
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		//FIXME ANN

		springFacesContext.render(new Renderable(getUrl()));
	}

	@Override
	public boolean checkResource(Locale locale) throws Exception {
		//FIXME check if the resource exists
		return super.checkResource(locale);
	}

	public String getViewId() {
		return getUrl();
	}

	public String getViewName() {
		return getBeanName();
	}
}
