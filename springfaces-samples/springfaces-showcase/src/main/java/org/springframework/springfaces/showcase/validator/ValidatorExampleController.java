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
		// Any SpringBean that implements Validator is automatically available as a JSF validator with fully
		// supported Spring dependency injection and AOP. Here SpringBeanConverter is used
		return new ExtendedModelMap().addAttribute("holder", new ValidatorObjectHolder());
	}

	@RequestMapping("/validator/genericspringbean")
	public Model genericSpringBean() {
		// An alternative Validator interface with generics can also be used with spring beans
		return new ExtendedModelMap().addAttribute("holder", new ValidatorObjectHolder());
	}

	@RequestMapping("/validator/springbeanforclass")
	public Model springBeanForClass() {
		// Spring Beans can use the @ForClass annotation to tie them to a specific class,
		// In this example SpringBeanForClassValidator will be used as it is annotated with
		// @ForClass and implements Validator<BigInteger>
		return new ExtendedModelMap().addAttribute("holder", new ValidatorObjectHolder());
	}
}
