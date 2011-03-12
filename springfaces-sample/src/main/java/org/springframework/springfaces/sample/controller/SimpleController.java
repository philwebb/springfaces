package org.springframework.springfaces.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("simple")
public class SimpleController {

	@RequestMapping(method = RequestMethod.GET)
	public void simple() {
	}

	@RequestMapping(method = RequestMethod.POST)
	public String postback() {
		return "test";
	}

}
