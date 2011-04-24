package org.springframework.springfaces.mvc.navigation;

import java.util.List;
import java.util.Map;

public class NavigationOutcome {

	private Object destination;
	private Map<String, List<String>> parameters;

	public NavigationOutcome(Object destination, Map<String, List<String>> parameters) {
		super();
		this.destination = destination;
		this.parameters = parameters;
	}

	public Object getDestination() {
		return this.destination;
	}

	public Map<String, List<String>> getParameters() {
		return this.parameters;
	}
}
