package org.springframework.springfaces.showcase.selectitems;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC Controller to demonstrate select items.
 * 
 * @author Phillip Webb
 */
@Controller
public class SelectItemsExampleController {

	@RequestMapping("/selectitems/test")
	public void implicitLink() {
		// Links are evaluated early and rendered as standard anchor link, no postback occurs.
	}
}
