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
package org.springframework.springfaces.showcase.exceptionhandler;

import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MVC Controller to demonstrate exception handling.
 * 
 * @author Phillip Webb
 */
@Controller
public class ExceptionHandlerExampleController {

	@RequestMapping("/exceptionhandler/handledelcall")
	public void handledElCall() {
		// Exception thrown from EL calls can be handled, in this case EL calls throwExampleException() and is handled
		// by the handle() @ExceptionHandler.
	}

	public String throwExampleException() {
		throw new ExampleException();
	}

	@RequestMapping("/exceptionhandler/handlednavigationmapping")
	public void handledNavigationMapping() {
		// Navigation mapping can also throw exceptions, handled in the same way
	}

	@NavigationMapping
	public String onThrowExampleException() {
		throw new ExampleException();
	}

	@RequestMapping("/exceptionhandler/messageelcall")
	public void messageElCall() {
		// Exceptions can also be mapped to FacesMessages, in this case EL calls messageNavigationMapping() and the
		// ExampleObjectMessageException is mapped in the ObjectMessageSource
	}

	public String throwExampleObjectMessageException() throws ExampleObjectMessageException {
		throw new ExampleObjectMessageException("Exception message", "EL");
	}

	@RequestMapping("/exceptionhandler/messagenavigationmapping")
	public void messageNavigationMapping() {
		// Again Navigation mappings support exception message mapping
	}

	@NavigationMapping
	public String onThrowExampleObjectMessageException() throws ExampleObjectMessageException {
		throw new ExampleObjectMessageException("Exception message", "Navigation");
	}

	@RequestMapping("/exceptionhandler/facesview")
	public void facesView() {
	}

	public void throwExampleFacesViewException() {
		throw new ExampleFacesViewException();
	}

	@ExceptionHandler
	public String handle(ExampleException e) {
		// ExampleException is handled by redirecting
		return "redirect:outcome";
	}

	@ExceptionHandler
	public String handleFacedView(ExampleFacesViewException e) {
		return "exceptionhandler/outcome";
	}

	@ExceptionHandler
	@ResponseBody
	public String handleResponseBody(ResponseBodyException e) {
		return "Error";
	}

	@RequestMapping("/exceptionhandler/outcome")
	public void outcome() {
	}
}
