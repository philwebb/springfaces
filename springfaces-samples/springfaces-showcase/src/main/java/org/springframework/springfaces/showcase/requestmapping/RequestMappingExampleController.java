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
package org.springframework.springfaces.showcase.requestmapping;

import java.util.HashMap;
import java.util.Map;

import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Demonstrates various types of {@link RequestMapping}. All standard MVC {@link RequestMapping}s are supported.
 * 
 * @author Phillip Webb
 */
@Controller
public class RequestMappingExampleController {

	@RequestMapping("/requestmapping/simple")
	public void simple() {
		// The view is generated from the mapping path
	}

	@RequestMapping("/requestmapping/string")
	public String string() {
		// The view name is deduced from the returned string
		return "requestmapping/mappedbyname";
	}

	@RequestMapping("/requestmapping/modelandview")
	public ModelAndView modelAndView() {
		// You can also return a FacesView for complete control
		View view = new FacesView("/WEB-INF/pages/requestmapping/modelandview.xhtml");
		Map<String, ?> model = new HashMap<String, Object>();
		return new ModelAndView(view, model);
	}

	@RequestMapping("/requestmapping/variables/{path}")
	public ModelAndView variables(@PathVariable String path, @RequestParam String argument) {
		// MVC Path variable and request parameters can be used, the model is availble from the page
		return new ModelAndView("requestmapping/variables", "argument", argument);
	}

	@RequestMapping("/requestmapping/postback")
	public String postback() {
		return "requestmapping/postback";
	}

}
