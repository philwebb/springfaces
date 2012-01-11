package org.springframework.springfaces.showcase.selectitems;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC Controller to demonstrate select items.
 * 
 * @author Phillip Webb
 */
@Controller
public class SelectItemsExampleController {

	@RequestMapping("/selectitems/selectonejpa")
	public Model selectOneJpa() {
		// Select items values can be bound to any bean, items contents can constructed dynamically using EL
		// expressions. In the case of JPA entities the ID is automatically used as the option value.
		// There is no need to add a converter
		return new ExtendedModelMap().addAttribute("exampleBean", new ExampleSelectItemsBean());
	}

	@RequestMapping("/selectitems/selectmanystring")
	public Model selectManyString() {
		// You can also use a simple comma separated string to create values
		return new ExtendedModelMap().addAttribute("exampleBean", new ExampleSelectItemsBean());
	}

	@RequestMapping("/selectitems/selectoneboolean")
	public Model selectOneBoolean() {
		// If you don't specify values they will be deduced from the value binding, here a menu
		// is populated with yes/no boolean values
		return new ExtendedModelMap().addAttribute("exampleBean", new ExampleSelectItemsBean());
	}

	@RequestMapping("/selectitems/selectmanyenum")
	public Model selectManyEnum() {
		// Items can also be deduced from any typed Collection. Here each enum value is presented as
		// a checkbox. ObjectMessageSource mapping is also supported
		return new ExtendedModelMap().addAttribute("exampleBean", new ExampleSelectItemsBean());
	}
}
