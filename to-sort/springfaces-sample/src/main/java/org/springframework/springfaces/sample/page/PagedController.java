package org.springframework.springfaces.sample.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paged")
public class PagedController {

	@RequestMapping("/pagedmyfaces")
	public void pagedmyfaces() {
	}

	@RequestMapping("/pagedprimefaces")
	public void pagedprimefaces() {
	}
}
