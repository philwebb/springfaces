/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.showcase.converter;

import javax.validation.Valid;

import org.springframework.springfaces.mvc.converter.FacesConverterId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * MVC Controller to demonstrate conversion.
 * 
 * @author Phillip Webb
 */
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
		// Any SpringBean that implements Converter is automatically available as a JSF converter with fully
		// supported Spring dependency injection and AOP. Here SpringBeanConverter is used
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/genericspringbean")
	public Model genericSpringBean() {
		// An alternative Converter interface with generics can also be used with spring beans
		// in this example the converter is bound directly to a component in the view
		// (see /converter/genericspringbean.xhtml and GenericSpringBeanConverter)
		return new ExtendedModelMap().addAttribute("holder", new ConverterObjectHolder());
	}

	@RequestMapping("/converter/springbeanforclass")
	public Model springBeanForClass(@RequestParam SpringConvertedObject value) {
		// Spring Beans can use the @ForClass annotation to tie them to a specific class,
		// In this example ForClassSpringConverter will be used as it is annotated with
		// @ForClass and implements Converter<SpringConvertedObject>
		return new ExtendedModelMap().addAttribute("converted", value.toString());
	}

	@RequestMapping("/converter/bindingerror")
	public void bindingError(@ModelAttribute("vadidated") @Valid ValidatedObject validatedObject) {
	}
}
