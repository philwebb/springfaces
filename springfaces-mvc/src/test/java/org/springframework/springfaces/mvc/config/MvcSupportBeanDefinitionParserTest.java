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
package org.springframework.springfaces.mvc.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.junit.Test;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolverChain;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolverChain;
import org.springframework.springfaces.mvc.render.ClientFacesViewStateHandler;
import org.springframework.springfaces.mvc.servlet.DefaultDispatcher;
import org.springframework.springfaces.mvc.servlet.Dispatcher;
import org.springframework.springfaces.mvc.servlet.DispatcherAware;
import org.springframework.springfaces.mvc.servlet.SpringFacesFactories;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests for {@link MvcSupportBeanDefinitionParser}.
 * 
 * @author Phillip Webb
 */
public class MvcSupportBeanDefinitionParserTest {

	@Test
	public void shouldUseSpecifiedDispatcher() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext(bean("dispatcher", CustomDispatcher.class)
						+ bean("aware", DispatcherAwareBean.class) + "<faces:mvc-support dispatcher=\"dispatcher\"/>");
		assertThat(applicationContext.getBean("dispatcher"), is(CustomDispatcher.class));
		assertThat(applicationContext.getBean(DispatcherAwareBean.class).getDispatcher(), is(CustomDispatcher.class));
	}

	@Test
	public void shouldUseSpecifiedStateHandler() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext(bean("statehandler", CustomFacesViewStateHandler.class)
						+ "<faces:mvc-support state-handler=\"statehandler\"/>");
		assertThat(applicationContext.getBean(SpringFacesFactories.class).getFacesViewStateHandler(),
				is(CustomFacesViewStateHandler.class));
	}

	@Test
	public void shouldRegisterCustomViewResolverWithDefaults() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext("<faces:mvc-support><faces:destination-view-resolvers>"
						+ bean("viewResolverBean", CustomDestinationViewResolver.class)
						+ "</faces:destination-view-resolvers></faces:mvc-support>");
		DestinationViewResolverChain chain = applicationContext.getBean(DestinationViewResolverChain.class);
		assertThat(chain.getResolvers().size(), is(3));
		assertThat(chain.getResolvers().get(0), is(CustomDestinationViewResolver.class));
	}

	@Test
	public void shouldRegisterCustomViewResolverWithoutDefaults() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext(bean("viewResolverBean", CustomDestinationViewResolver.class)
						+ "<faces:mvc-support><faces:destination-view-resolvers register-defaults=\"false\"><ref bean=\"viewResolverBean\"/>"
						+ "</faces:destination-view-resolvers></faces:mvc-support>");
		DestinationViewResolverChain chain = applicationContext.getBean(DestinationViewResolverChain.class);
		assertThat(chain.getResolvers().size(), is(1));
		assertThat(chain.getResolvers().get(0), is(CustomDestinationViewResolver.class));
	}

	@Test
	public void shouldRegisterCustomNavigationViewResolverWithDefaults() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext("<faces:mvc-support><faces:navigation-outcome-resolvers>"
						+ bean("navigationOutcomeResolverBean", CustomNavigationOutcomeResolver.class)
						+ "</faces:navigation-outcome-resolvers></faces:mvc-support>");
		NavigationOutcomeResolverChain chain = applicationContext.getBean(NavigationOutcomeResolverChain.class);
		assertThat(chain.getResolvers().size(), is(3));
		assertThat(chain.getResolvers().get(0), is(CustomNavigationOutcomeResolver.class));
	}

	@Test
	public void shouldRegisterCustomNavigationViewResolverWithoutDefaults() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext(bean("navigationOutcomeResolverBean", CustomNavigationOutcomeResolver.class)
						+ "<faces:mvc-support><faces:navigation-outcome-resolvers register-defaults=\"false\"><ref bean=\"navigationOutcomeResolverBean\"/>"
						+ "</faces:navigation-outcome-resolvers></faces:mvc-support>");
		NavigationOutcomeResolverChain chain = applicationContext.getBean(NavigationOutcomeResolverChain.class);
		assertThat(chain.getResolvers().size(), is(1));
		assertThat(chain.getResolvers().get(0), is(CustomNavigationOutcomeResolver.class));
	}

	private String bean(String beanName, Class<?> beanClass) {
		return "<bean name=\"" + beanName + "\" class=\"" + beanClass.getName() + "\"/>";
	}

	public static class CustomDispatcher extends DefaultDispatcher {
	}

	public static class DispatcherAwareBean implements DispatcherAware {
		private Dispatcher dispatcher;

		public void setDispatcher(Dispatcher dispatcher) {
			this.dispatcher = dispatcher;
		}

		public Dispatcher getDispatcher() {
			return this.dispatcher;
		}
	}

	public static class CustomFacesViewStateHandler extends ClientFacesViewStateHandler {
	}

	private static class CustomDestinationViewResolver implements DestinationViewResolver {

		public ModelAndView resolveDestination(FacesContext context, Object destination, Locale locale,
				SpringFacesModel model) throws Exception {
			return null;
		}
	}

	private static class CustomNavigationOutcomeResolver implements NavigationOutcomeResolver {

		public boolean canResolve(FacesContext facesContext, NavigationContext navigationContext) {
			return false;
		}

		public NavigationOutcome resolve(FacesContext facesContext, NavigationContext navigationContext)
				throws Exception {
			return null;
		}

	}
}
