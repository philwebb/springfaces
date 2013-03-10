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
package org.springframework.springfaces.showcase.selectitems;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC Controller to demonstrate select items.
 * 
 * @author Phillip Webb
 * @author Pedro Casagrande de Campos
 */
@Controller
public class SelectItemsExampleController {

	@RequestMapping("/selectitems/selectonejpapartial")
	public Model selectOneJpaPartial() {
		return new ExtendedModelMap().addAttribute("exampleBean", new ExampleSelectItemsBean());
	}

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
