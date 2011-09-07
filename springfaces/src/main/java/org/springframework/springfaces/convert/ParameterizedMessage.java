package org.springframework.springfaces.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ParameterizedMessage {

	private static final Pattern PATTERN = Pattern.compile("\\{([\\w]+?)\\}");

	private static final ParameterizedMessage NONE = new ParameterizedMessage(PATTERN.matcher(""));

	private static Map<String, ParameterizedMessage> cache = new ConcurrentHashMap<String, ParameterizedMessage>();

	private List<Object> sections = new ArrayList<Object>();

	private ParameterizedMessage(Matcher matcher) {
		matcher.reset();
		StringBuffer variable = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			sb.setLength(0);
			variable.setLength(0);
			variable.append(matcher.group(0).substring(1));
			variable.setLength(variable.length() - 1);
			matcher.appendReplacement(sb, "");
			sections.add(sb.toString());
			sections.add(new Variable(variable.toString()));
		}
		sb.setLength(0);
		matcher.appendTail(sb);
		sections.add(sb.toString());
	}

	public String resolve(VariableResolver resolver) {
		StringBuffer sb = new StringBuffer();
		for (Object section : sections) {
			sb.append(section instanceof Variable ? ((Variable) section).resolve(resolver) : section.toString());
		}
		return sb.toString();
	}

	public static ParameterizedMessage get(String message) {
		ParameterizedMessage parameterizedMessage = cache.get(message);
		if (parameterizedMessage == null) {
			parameterizedMessage = NONE;
			Matcher matcher = PATTERN.matcher(message);
			if (matcher.find()) {
				parameterizedMessage = new ParameterizedMessage(matcher);
			}
			cache.put(message, parameterizedMessage);
		}
		return (parameterizedMessage == NONE ? null : parameterizedMessage);
	}

	public static interface VariableResolver {
		public String resolve(String variable);
	}

	private static class Variable {

		private String name;

		public Variable(String name) {
			this.name = name;
		}

		public String resolve(VariableResolver resolver) {
			return resolver.resolve(name);
		}
	}

}
