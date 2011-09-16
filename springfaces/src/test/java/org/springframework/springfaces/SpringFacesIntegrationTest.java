package org.springframework.springfaces;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringFacesIntegration}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringFacesIntegrationTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private SpringFacesIntegration springFacesIntegration;

	private WebApplicationContext applicationContext;

	private ServletContext servletContext;

	private ExternalContext externalContext;

	@Captor
	private ArgumentCaptor<ApplicationEvent> applicationEventCaptor;

	public SpringFacesIntegrationTest() {
		servletContext = new MockServletContext();
		externalContext = mock(ExternalContext.class);
		Map<String, Object> applicationMap = new AbstractMap<String, Object>() {
			@Override
			public Set<java.util.Map.Entry<String, Object>> entrySet() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object get(Object key) {
				return servletContext.getAttribute((String) key);
			}

			@Override
			public Object put(String key, Object value) {
				servletContext.setAttribute(key, value);
				return null;
			}
		};
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
	}

	private void createSpringFacesIntegration() {
		this.applicationContext = mock(WebApplicationContext.class);
		given(applicationContext.getServletContext()).willReturn(servletContext);
		this.springFacesIntegration = new SpringFacesIntegration();
		springFacesIntegration.setApplicationContext(applicationContext);
	}

	@Test
	public void shouldNotBeInstalledUsing() throws Exception {
		assertFalse(SpringFacesIntegration.isInstalled(servletContext));
	}

	@Test
	public void shouldNotBeInstalledUsingExternalContext() throws Exception {
		ExternalContext externalContext = mock(ExternalContext.class);
		assertFalse(SpringFacesIntegration.isInstalled(externalContext));
	}

	@Test
	public void shouldBeInstalledUsing() throws Exception {
		createSpringFacesIntegration();
		assertTrue(SpringFacesIntegration.isInstalled(servletContext));
	}

	@Test
	public void shouldBeInstalledUsingExternalContext() throws Exception {
		createSpringFacesIntegration();
		assertTrue(SpringFacesIntegration.isInstalled(externalContext));
	}

	@Test
	public void shouldThrowWithoutLastRefreshedDate() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to determine the last refresh date for SpringFaces");
		SpringFacesIntegration.getLastRefreshedDate(servletContext);
	}

	@Test
	public void shouldThrowWithoutLastRefreshedDateFromExternalContext() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to determine the last refresh date for SpringFaces");
		SpringFacesIntegration.getLastRefreshedDate(externalContext);
	}

	@Test
	public void shouldHaveSetLastRefreshedDateOnLoad() throws Exception {
		createSpringFacesIntegration();
		assertNotNull(SpringFacesIntegration.getLastRefreshedDate(servletContext));
		assertNotNull(SpringFacesIntegration.getLastRefreshedDate(externalContext));
	}

	@Test
	public void shouldUpdateLastRefreshDateOnReload() throws Exception {
		createSpringFacesIntegration();
		Date initialDate = SpringFacesIntegration.getLastRefreshedDate(servletContext);
		Thread.sleep(100);
		ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
		springFacesIntegration.onApplicationEvent(event);
		assertTrue(SpringFacesIntegration.getLastRefreshedDate(servletContext).after(initialDate));
		assertTrue(SpringFacesIntegration.getLastRefreshedDate(externalContext).after(initialDate));
	}

	@Test
	public void shouldGetCurrentInstace() throws Exception {
		createSpringFacesIntegration();
		assertSame(springFacesIntegration, SpringFacesIntegration.getCurrentInstance(servletContext));
		assertSame(springFacesIntegration, SpringFacesIntegration.getCurrentInstance(externalContext));
	}

	@Test
	public void shouldPublishPostConstructApplicationEventWhenSpringFirst() throws Exception {
		createSpringFacesIntegration();
		Application application = mock(Application.class);
		SpringFacesIntegration.postConstructApplicationEvent(externalContext, application);
		verify(applicationContext).publishEvent(applicationEventCaptor.capture());
		assertSame(application, applicationEventCaptor.getValue().getSource());
	}

	@Test
	public void shouldPublishPostConstructApplicationEventWhenJsfFirst() throws Exception {
		Application application = mock(Application.class);
		SpringFacesIntegration.postConstructApplicationEvent(externalContext, application);
		createSpringFacesIntegration();
		springFacesIntegration.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
		verify(applicationContext).publishEvent(applicationEventCaptor.capture());
		assertSame(application, applicationEventCaptor.getValue().getSource());
	}
}
