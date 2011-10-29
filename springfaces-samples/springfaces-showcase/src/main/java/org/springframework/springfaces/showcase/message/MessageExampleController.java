package org.springframework.springfaces.showcase.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
