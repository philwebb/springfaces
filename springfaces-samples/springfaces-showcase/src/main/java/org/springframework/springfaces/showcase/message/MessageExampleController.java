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
package org.springframework.springfaces.showcase.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC Controller to demonstrate messages.
 * 
 * @author Phillip Webb
 */
@Controller
public class MessageExampleController {

	@RequestMapping("/message/simple")
	public void simple() {
		// The messageSource component can be used to access messages from the ApplicationContext
		// by default the view ID is used to generate a code prefix
	}

	@RequestMapping("/message/prefix")
	public void prefix() {
		// You can define the code prefix yourself if required
	}

	@RequestMapping("/message/definedsource")
	public void definedsource() {
		// You don't have to use the ApplicationContext as the message source, just reference the
		// source that you do want. Here we use a StaticMessageSource defined in CustomMessageSourceConfiguration
	}

	@RequestMapping("/message/parameters")
	public void parameters() {
		// Messages are exposed as a MessageSourceMap so you can easily expand parameters
		// The last resolved paramter is always a String
	}

	@RequestMapping("/message/objects")
	public void objects() {
		// Objects can also be used as keys into the message map
	}

	@RequestMapping("/message/missing")
	public void missing() {
		// When not running in production missing messages are displayed as warning messages
	}

}
