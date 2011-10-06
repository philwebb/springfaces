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
	public Model facesForClass(@RequestParam ConvertedObject value) {
		// Request parameters are automatically converted using JSF converters for the class
		// Here the ForClassFacesConverter is used because it is has the JSF
		// @FacesConverter(forClass = ConvertedObject.class) annotation
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/facesbyid")
	public Model facesById(@FacesConverterId("byId") @RequestParam ConvertedObject value) {
		// You can also use @FacesConverterId to use a specific JSF converter
		// Here the ByIdFacesConverter is used because it is has the JSF
		// @FacesConverter("byId") annotation
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/springbean")
	public Model springBean(@FacesConverterId("springBeanConverter") @RequestParam ConvertedObject value) {
		// Any SpringBean that implements Converter is automatically available as a JSF converter fully
		// supporting Spring dependency injection and AOP. Here SpringBeanConverter is used
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/genericspringbean")
	public Model genericSpringBean() {
		// An alternative Converter interface with generics can also be used with spring beans
		// in this example the converter is bound directly to a component in the view
		// (see /converter/genericspringbean.xhtml and GenericSpringBeanConverter)
		return new ExtendedModelMap().addAttribute("holder", new ConvertedObjectHolder());
	}

	@RequestMapping("/converter/springbeanforclass")
	public Model springBeanForClass(@RequestParam SpringConvertedObject value) {
		// Spring Beans can use the @ForClass annotation to tie them to a specific class,
		// In this example ForClassSpringConverter will be used as it is annotated with
		// @ForClass and implements Converter<SpringConvertedObject>
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}
}
