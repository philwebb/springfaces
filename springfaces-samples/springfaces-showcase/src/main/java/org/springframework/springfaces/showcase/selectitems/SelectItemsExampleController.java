package org.springframework.springfaces.showcase.selectitems;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC Controller to demonstrate select items.
 * 
 * @author Phillip Webb
 */
@Controller
public class SelectItemsExampleController {

	@RequestMapping("/selectitems/test")
	public Model implicitLink() {
		// Links are evaluated early and rendered as standard anchor link, no postback occurs.
		ExtendedModelMap model = new ExtendedModelMap();
		model.put("sampleBean", new SampleBean());
		return model;
	}
}
