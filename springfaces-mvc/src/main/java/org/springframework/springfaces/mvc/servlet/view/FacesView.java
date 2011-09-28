package org.springframework.springfaces.mvc.servlet.view;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.validation.AbstractPropertyBindingResult;
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
		// FIXME model contains non serializable binding results, should these even be exposed
		// or should we translate them to FacesMessages? or throw an exception?
		// HACK : for now we just remove them
		model = new LinkedHashMap<String, Object>(model);
		for (Iterator<Map.Entry<String, Object>> iterator = model.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getValue() instanceof AbstractPropertyBindingResult) {
				iterator.remove();
			}
		}
		ViewArtifact viewArtifact = new ViewArtifact(getUrl());
		SpringFacesContext.getCurrentInstance(true).render(new ModelAndViewArtifact(viewArtifact, model));
	}

	@Override
	public boolean checkResource(Locale locale) throws Exception {
		// FIXME check if the resource exists
		return super.checkResource(locale);
	}

	public String getViewId() {
		return getUrl();
	}

	public String getViewName() {
		return getBeanName();
	}
}
