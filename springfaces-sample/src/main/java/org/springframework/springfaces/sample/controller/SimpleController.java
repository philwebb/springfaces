package org.springframework.springfaces.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SimpleController {

	@RequestMapping("simple/{name}")
	//@Bookmark("simple")
	public String simple(@PathVariable Name name, Model model) {
		System.out.println(name);
		Hotel hotel = new Hotel();
		hotel.setId(123L);
		model.addAttribute(hotel);
		return "simple";
	}

	@RequestMapping("redirect")
	public String redirect() {
		return "bookmarkRedirect:simple";
	}

	@RequestMapping("hello")
	public String hello() {
		return "testBeanView";
	}

	//	@RequestMapping(method = RequestMethod.POST)
	//	public String postback() {
	//		return "test";
	//	}

}
