package org.springframework.springfaces.showcase.validator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC Controller to demonstrate validation.
 * 
 * @author Phillip Webb
 */
@Controller
public class ValidatorExampleController {

	@RequestMapping("/validator/springbean")
	public Model springBean() {
		return new ExtendedModelMap().addAttribute("holder", new ValidatorObjectHolder());
	}

	@RequestMapping("/validator/genericspringbean")
	public Model genericSpringBean() {
		return new ExtendedModelMap().addAttribute("holder", new ValidatorObjectHolder());
	}

	@RequestMapping("/validator/springbeanforclass")
	public Model springBeanForClass() {
		return new ExtendedModelMap().addAttribute("holder", new ValidatorObjectHolder());
	}
}
