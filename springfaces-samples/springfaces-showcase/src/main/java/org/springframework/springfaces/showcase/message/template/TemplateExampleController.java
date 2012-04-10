package org.springframework.springfaces.showcase.message.template;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TemplateExampleController {

	@RequestMapping("/template/decorateall")
	public void simple() {
	}

	@RequestMapping("/template/componentinfo")
	public void componentInfo() {
	}

}
