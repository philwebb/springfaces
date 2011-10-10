package org.springframework.springfaces.showcase.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MessageExampleController {

	@RequestMapping("/message/simple")
	public void simple() {
	}

	@RequestMapping("/message/paramters")
	public void paramters() {
	}

	@RequestMapping("/message/missing")
	public void missing() {
	}

}
