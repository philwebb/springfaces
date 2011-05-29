package org.springframework.springfaces.mvc.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

public class MvcFacesTestUtils {

	public static interface MethodCallAssertor {

		public void recordMethodCall(Method method);

		public void assertCalled(String methodName);

		public void assertCalled(String[] methodNames);

		public void assertNotCalled(String methodName);
	}

	public static class MethodTrackerInterceptor extends DelegatingIntroductionInterceptor implements
			MethodCallAssertor {

		private static final long serialVersionUID = 1L;

		private Set<String> called = new HashSet<String>();

		public void recordMethodCall(Method method) {
			called.add(method.getName());
		}

		public void assertCalled(String methodName) {
			assertCalled(new String[] { methodName });
		}

		public void assertCalled(String[] methods) {
			Assert.assertEquals(new HashSet<String>(Arrays.asList(methods)), called);
		}

		public void assertNotCalled(String methodName) {
			Assert.assertFalse(called.contains(methodName));
		}

	}

	public static Object nullImplementation(Class<?> targetClass) {
		return nullImplementation(targetClass, null);
	}

	public static Object nullImplementation(Class<?> targetClass, final MethodInterceptor methodInterceptor) {
		AdvisedSupport aopConfig = new AdvisedSupport();
		aopConfig.setTargetClass(targetClass);
		aopConfig.setProxyTargetClass(true);
		aopConfig.addAdvice(new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				Object rtn = null;
				if (methodInterceptor != null) {
					rtn = methodInterceptor.invoke(invocation);
				}
				return rtn;
			}
		});

		DefaultAopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
		AopProxy proxy = aopProxyFactory.createAopProxy(aopConfig);
		return proxy.getProxy();
	}

	public static Object methodTrackingObject(Object target) {

		AdvisedSupport aopConfig = new AdvisedSupport();
		aopConfig.setTarget(target);
		aopConfig.setProxyTargetClass(true);

		aopConfig.addAdvice(new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				Object proxy = ((ProxyMethodInvocation) invocation).getProxy();
				if (!MethodCallAssertor.class.equals(invocation.getMethod().getDeclaringClass())) {
					((MethodCallAssertor) proxy).recordMethodCall(invocation.getMethod());
				}
				return invocation.proceed();
			}
		});
		aopConfig.addAdvice(new MethodTrackerInterceptor());

		DefaultAopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
		AopProxy proxy = aopProxyFactory.createAopProxy(aopConfig);
		return proxy.getProxy();
	}

	public static Object methodTrackingObject(Class<?> targetClass) {
		Object nullImplementation = nullImplementation(targetClass);
		return methodTrackingObject(nullImplementation);
	}

	public static void callMethods(Object object, String[] methods) throws Exception {
		Set<String> methodNames = new HashSet<String>(Arrays.asList(methods));
		Method[] classMethods = object.getClass().getMethods();
		for (int i = 0; i < classMethods.length; i++) {
			if (methodNames.contains(classMethods[i].getName())) {
				classMethods[i].invoke(object, new Object[classMethods[i].getParameterTypes().length]);
			}
		}
	}
}
