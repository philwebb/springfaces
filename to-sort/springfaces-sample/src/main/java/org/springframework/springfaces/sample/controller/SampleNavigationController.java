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
package org.springframework.springfaces.sample.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
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

	@NavigationMapping
	public NavigationOutcome onAnnotationLink() {
		return new NavigationOutcome("@destination", Collections.<String, Object> singletonMap("s", "from annotation"));
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

	@NavigationMapping
	public HttpEntity<String> onAnnotationHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Disposition", "attachment; filename=\"test.txt\"");
		return new HttpEntity<String>("test", headers);
	}
}
