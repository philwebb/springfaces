package org.springframework.springfaces.showcase.converter;

import org.springframework.springfaces.mvc.converter.FacesConverterId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConverterExampleController {

	@RequestMapping("/converter/facesforclass")
	public Model simple(@RequestParam ConvertedObject value) {
		// Request parameters are automatically converted using JSF converters for the class
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/facesbyid")
	public Model byId(@FacesConverterId("byId") @RequestParam ConvertedObject value) {
		// You can also use @FacesConverterId to use a specific JSF converter
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/springbean")
	public Model springBean(@FacesConverterId("springBeanConverter") @RequestParam ConvertedObject value) {
		// Any SpringBean that implements Converter is automatically available as a JSF converter
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/genericspringbean")
	public Model genericSpringBean() {
		// An alternative Converter interface with generics can also be used with spring beans
		// in this example the converter is bound directly to a component in the view
		// (see /converter/genericspringbean.xhtml and GenericSpringBeanConverter)
		return new ExtendedModelMap().addAttribute("holder", new ConvertedObjectHolder());
	}
}
