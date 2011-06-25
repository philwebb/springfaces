package org.springframework.springfaces.sample.controller;

import java.io.IOException;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/navigation")
public class SampleNavigationController {

	@RequestMapping("/type")
	public void navigationTypes(ModelMap model) {
		model.put("navigationBean", new NavigationBean());
	}

	@RequestMapping("/destination")
	public void destination(@RequestParam(required = false) String s, ModelMap model) {
		model.put("s", s);
	}

	public String directNavigation() {
		return "spring:redirect:http://www.springsource.org";
	}

	@NavigationMapping("annotationwithvalue")
	public String navigationAnnotation(@Value("#{navigationBean}") NavigationBean navigationBean) {
		return "redirect:" + navigationBean.getDestination();
	}

	// FIXME change to inject model. Can we detect model items by type
	@NavigationMapping("annotationrerender")
	public void navigationReRender(@Value("#{navigationBean}") NavigationBean navigationBean) {
		navigationBean.setDate(new Date());
	}

	// FIXME we should support injection of Writer and OutputStream, and HttpServletResponse
	@NavigationMapping
	public void onAnnotationStream(FacesContext context, HttpServletResponse response) throws IOException {
		response.setContentType("binary/octet-stream");
		response.setContentLength(5);
		response.setHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
		response.getWriter().write("hello");
		response.flushBuffer();
		context.responseComplete();
	}

	@NavigationMapping
	@ResponseBody
	public String onAnnotationResponseBody() {
		return "hello";
	}
}
