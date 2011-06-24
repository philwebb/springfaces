package org.springframework.springfaces.mvc.navigation.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public abstract class ImplicitObjectMethodArgumentResolver implements HandlerMethodArgumentResolver {

	protected static final Callable<Boolean> ALWAYS = new Callable<Boolean>() {
		public Boolean call() throws Exception {
			return Boolean.TRUE;
		}
	};

	private List<ImplicitObject<?>> implicitObjects = new ArrayList<ImplicitObject<?>>();

	protected final <T> void add(Class<T> type, Callable<T> call) {
		add(type, ALWAYS, call);
	}

	protected final <T> void add(Class<T> type, Callable<Boolean> condition, Callable<T> call) {
		Assert.notNull(type, "Type must not be null");
		Assert.notNull(condition, "Condition must not be null");
		Assert.notNull(call, "Call must not be null");
		implicitObjects.add(new ImplicitObject<T>(type, condition, call));
	}

	public boolean supportsParameter(MethodParameter parameter) {
		try {
			for (ImplicitObject<?> implicitObject : implicitObjects) {
				if (implicitObject.getType().isAssignableFrom(parameter.getParameterType())
						&& Boolean.TRUE.equals(implicitObject.getCondition().call())) {
					return true;
				}
			}
		} catch (Exception e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		return false;
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		for (ImplicitObject<?> implicitObject : implicitObjects) {
			if (implicitObject.getType().isAssignableFrom(parameter.getParameterType())
					&& Boolean.TRUE.equals(implicitObject.getCondition().call())) {
				return implicitObject.getCall().call();
			}
		}
		return null;
	}

	private static class ImplicitObject<T> {
		private Class<T> type;
		private Callable<Boolean> condition;
		private Callable<T> call;

		public ImplicitObject(Class<T> type, Callable<Boolean> condition, Callable<T> call) {
			this.type = type;
			this.condition = condition;
			this.call = call;
		}

		public Class<T> getType() {
			return type;
		}

		public Callable<Boolean> getCondition() {
			return condition;
		}

		public Callable<T> getCall() {
			return call;
		}
	}

}
