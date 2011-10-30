package org.springframework.springfaces.showcase.navigation;

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

/**
 * MVC Controller to demonstrate navigation.
 * 
 * @author Phillip Webb
 */
@Controller
public class NavigationExampleController {

	// Implicit destinations can be encoded inside the JSF page using the prefix "spring:"

	@RequestMapping("/navigation/implicitlink")
	public void implicitLink() {
		// Links are evaluated early and rendered as standard anchor link, no postback occurs.
	}

	@RequestMapping("/navigation/implicitbutton")
	public void implicitButton() {
		// Buttons are evaluated early, no postback occurs.
	}

	@RequestMapping("/navigation/implicitcommandlink")
	public void implicitCommandLink() {
		// Command links use javascript to trigger a postback before navigation
	}

	@RequestMapping("/navigation/implicitcommandbutton")
	public void implicitCommandButton() {
		// Command buttons trigger a postback before navigation
	}

	@RequestMapping("/navigation/implicitmvcredirect")
	public void implicitMvcRedirect() {
		// Any MVC resolvable view ID can be used as an implicit destination, here we use
		// "redirect:/<url>" triggering the BookmarkableRedirectViewIdResolver
	}

	// Spring destinations can also be used with standard JSF navigation rules. The following
	// pick up destinations from the navigation-rules section of faces-config.xml

	@RequestMapping("/navigation/rulelink")
	public void ruleLink() {
	}

	@RequestMapping("/navigation/rulebutton")
	public void ruleButton() {
	}

	@RequestMapping("/navigation/rulecommandlink")
	public void ruleCommandLink() {
	}

	@RequestMapping("/navigation/rulecommandbutton")
	public void ruleCommandButton() {
	}

	// Navigation outcomes can be EL expression, it is possible to call methods
	// on the controller to obtain the navigation outcome

	@RequestMapping("/navigation/direct")
	public void direct() {
		// The el expression #{controller.directNavigation} calls the method below
	}

	public String directNavigation() {
		return "spring:redirect:/spring/navigation/destination?s=direct";
	}

	// Navigation outcomes can also be defined using the @NavigationMapping annotation

	@RequestMapping("/navigation/annotationlink")
	public void annotationLink() {
		// The outcome is used to search for @NavigationMappings
	}

	@NavigationMapping
	public NavigationOutcome onAnnotationLink() {
		// The the logic outcome is "annotationLink" is determined from the method name
		return new NavigationOutcome("@destination", Collections.<String, Object> singletonMap("s", "from annotation"));
	}

	@RequestMapping("/navigation/annotationwithvalue")
	public void annotationWithValue(ModelMap model) {
		// The @Value annotation can be used to reference backing beans
		model.put("navigationBean", new NavigationBean());
	}

	@NavigationMapping("annotationwithvalue")
	public String navigationAnnotation(@Value("#{navigationBean}") NavigationBean navigationBean) {
		// We dynamically redirect to whatever URL was entered
		return "redirect:" + navigationBean.getDestination();
	}

	@RequestMapping("/navigation/annotationrerender")
	public void annotationReRender(ModelMap model) {
		// @NavigationMapping methods that return void will simply re-render the current page
		model.put("navigationBean", new NavigationBean());
	}

	// FIXME change to inject model. Can we detect model items by type?
	@NavigationMapping("annotationrerender")
	public void navigationReRender(@Value("#{navigationBean}") NavigationBean navigationBean) {
		navigationBean.setDate(new Date());
	}

	@RequestMapping("/navigation/annotationstreaming")
	public void annotationStreaming() {
		// @NavigationMapping methods can take complete control of streaming the rendered response
	}

	@NavigationMapping
	public void onAnnotationStream(FacesContext context, HttpServletResponse response) throws IOException {
		response.setContentType("binary/octet-stream");
		response.setContentLength(5);
		response.setHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
		response.getWriter().write("hello");
		response.flushBuffer();
		context.responseComplete();
	}

	@RequestMapping("/navigation/annotationresponsebody")
	public void annotationResponseBody() {
		// Using @NavigationMapping with @ResponseBody is permitted
	}

	@NavigationMapping
	@ResponseBody
	public String onAnnotationResponseBody() {
		return "responsebody";
	}

	@RequestMapping("/navigation/annotationhttpentity")
	public void annotationHttpEntity() {
		// @NavigationMapping methods can also return HttpEntity objects
	}

	@NavigationMapping
	public HttpEntity<String> onAnnotationHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Disposition", "attachment; filename=\"test.txt\"");
		return new HttpEntity<String>("test", headers);
	}

	@RequestMapping("/navigation/destination")
	public void destination(@RequestParam(required = false) String s, ModelMap model) {
		// This mapping does not demonstrate navigation, it is used as the destination of other examples
		model.put("s", s);
	}
}
