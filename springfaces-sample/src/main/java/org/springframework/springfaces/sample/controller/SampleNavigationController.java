package org.springframework.springfaces.sample.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/navigation")
public class SampleNavigationController {

	@RequestMapping("/type")
	public void navigationTypes(ModelMap model) {
		model.put("navigationBean", new NavigationBean());
	}

	@RequestMapping("/destination")
	public void destination(@RequestParam(required = false) String s, ModelMap model) {
		model.put("s", s);
	}

	@NavigationMapping("annotationwithvalue")
	public String navigationAnnotation(@Value("#{navigationBean}") NavigationBean navigationBean) {
		return "redirect:" + navigationBean.getDestination();
	}
}
