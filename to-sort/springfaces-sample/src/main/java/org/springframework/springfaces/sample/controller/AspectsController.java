package org.springframework.springfaces.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aspects")
public class AspectsController {

	@RequestMapping("/example")
	public void example() {
	}

}
