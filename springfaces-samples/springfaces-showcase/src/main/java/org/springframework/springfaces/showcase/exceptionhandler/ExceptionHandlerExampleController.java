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

@Controller
public class ExceptionHandlerExampleController {

	@RequestMapping("/exceptionhandler/handledelcall")
	public void handledElCall() {
	}

	public String throwExampleException() {
		throw new ExampleException();
	}

	@RequestMapping("/exceptionhandler/handlednavigationmapping")
	public void handledNavigationMapping() {
	}

	@NavigationMapping
	public String onThrowExampleException() {
		throw new ExampleException();
	}

	@RequestMapping("/exceptionhandler/messageelcall")
	public void messageElCall() {
	}

	public String throwExampleObjectMessageException() throws ExampleObjectMessageException {
		throw new ExampleObjectMessageException("Example from EL", "Webb");
	}

	@RequestMapping("/exceptionhandler/messagenavigationmapping")
	public void messageNavigationMapping() {
	}

	public String onThrowExampleObjectMessageException() throws ExampleObjectMessageException {
		throw new ExampleObjectMessageException("Example from navigation", "Phil");
	}

	@ExceptionHandler
	public String handle(ExampleException e) {
		return "redirect:outcome";
	}

	@RequestMapping("/exceptionhandler/outcome")
	public void outcome() {
	}
}
