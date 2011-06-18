package org.springframework.springfaces.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/navigation")
public class SampleNavigationController {

	@RequestMapping("/type")
	public void navigationTypes() {
	}

	@RequestMapping("/destination")
	public void destination(@RequestParam(required = false) String s, ModelMap model) {
		model.put("s", s);
	}
}
