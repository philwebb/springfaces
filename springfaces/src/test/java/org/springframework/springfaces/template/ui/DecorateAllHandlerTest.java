package org.springframework.springfaces.template.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.view.Location;
import javax.faces.view.facelets.CompositeFaceletHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributeException;
import javax.faces.view.facelets.TagAttributes;
import javax.faces.view.facelets.TagConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.template.ui.DecorateAllHandler.DecoratedChild;
import org.springframework.springfaces.template.ui.DecorateAllHandler.Delegate;
import org.springframework.springfaces.template.ui.DecorateAllHandler.Type;

import com.sun.faces.facelets.tag.TagAttributesImpl;

/**
 * Tests for {@link DecorateAllHandler}.
 * 
 * @author Phillip Webb
 */
public class DecorateAllHandlerTest {

	private static final String TEMPLATE = "template.xhtml";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Delegate delegate;

	@Mock
	private TagConfig tagConfig;

	@Mock
	private FaceletContext ctx;

	@Mock
	private UIComponent parent;

	@Mock
	private TagAttribute templateTag;

	@Mock
	private DecorateAllHandler decorateAllHandler;

	@Mock
	private FaceletHandler component1;

	@Mock
	private FaceletHandler component2;

	@Mock
	private DecoratedChild decoratedComponent1;

	@Mock
	private DecoratedChild decoratedComponent2;

	@Captor
	private ArgumentCaptor<List<FaceletHandler>> variableDeclarationHandlers1;

	@Captor
	private ArgumentCaptor<List<FaceletHandler>> variableDeclarationHandlers2;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		String namespace = "";
		Location location = new Location("/path/to/file.xhtml", 100, 50);
		String localName = "decorateAll";
		String qName = "decorateAll";
		given(this.templateTag.getNamespace()).willReturn(namespace);
		given(this.templateTag.getLocalName()).willReturn("template");
		given(this.templateTag.getValue(this.ctx)).willReturn(TEMPLATE);
		TagAttributes attributes = new TagAttributesImpl(new TagAttribute[] { this.templateTag });
		Tag tag = new Tag(location, namespace, localName, qName, attributes);
		given(this.tagConfig.getTag()).willReturn(tag);
		given(this.delegate.getType(this.component1)).willReturn(Type.COMPONENT);
		given(this.delegate.createdDecoratedChild(eq(this.component1), this.variableDeclarationHandlers1.capture()))
				.willReturn(this.decoratedComponent1);
		given(this.delegate.getType(this.component2)).willReturn(Type.COMPONENT);
		given(this.delegate.createdDecoratedChild(eq(this.component2), this.variableDeclarationHandlers2.capture()))
				.willReturn(this.decoratedComponent2);
	}

	@Test
	public void shouldDecoreateSingleComponent() throws Exception {
		given(this.tagConfig.getNextHandler()).willReturn(this.component1);
		createDecorateAllHandlerAndApply();
		verify(this.decoratedComponent1).apply(this.ctx, this.parent, TEMPLATE);
	}

	@Test
	public void shouldDecorateMultipleComponent() throws Exception {
		CompositeFaceletHandler nextHandler = new CompositeFaceletHandler(new FaceletHandler[] { this.component1,
				this.component2 });
		given(this.tagConfig.getNextHandler()).willReturn(nextHandler);
		createDecorateAllHandlerAndApply();
		verify(this.decoratedComponent1).apply(this.ctx, this.parent, TEMPLATE);
		verify(this.decoratedComponent2).apply(this.ctx, this.parent, TEMPLATE);
	}

	@Test
	public void shouldPreserveUnknown() throws Exception {
		FaceletHandler unknown1 = mockFaceletHandler("unknown1", Type.OTHER);
		FaceletHandler unknown2 = mockFaceletHandler("unknown2", Type.OTHER);
		CompositeFaceletHandler nextHandler = new CompositeFaceletHandler(new FaceletHandler[] { unknown1,
				this.component1, unknown2 });
		given(this.tagConfig.getNextHandler()).willReturn(nextHandler);
		createDecorateAllHandlerAndApply();
		InOrder inOrder = inOrder(unknown1, this.decoratedComponent1, unknown2);
		inOrder.verify(unknown1).apply(this.ctx, this.parent);
		inOrder.verify(this.decoratedComponent1).apply(this.ctx, this.parent, TEMPLATE);
		inOrder.verify(unknown2).apply(this.ctx, this.parent);
	}

	@Test
	public void shouldDefaultWithEmptyDefinedParameters() throws Exception {
		given(this.tagConfig.getNextHandler()).willReturn(this.component1);
		createDecorateAllHandlerAndApply();
		assertThat(this.variableDeclarationHandlers1.getValue().size(), is(0));
	}

	@Test
	public void shouldPassDefinedParameters() throws Exception {
		FaceletHandler global1 = mockFaceletHandler("global1", Type.VARIABLE_DECLARATION);
		FaceletHandler global2 = mockFaceletHandler("global2", Type.VARIABLE_DECLARATION);
		FaceletHandler local1 = mockFaceletHandler("local1", Type.VARIABLE_DECLARATION);
		FaceletHandler local2 = mockFaceletHandler("local2", Type.VARIABLE_DECLARATION);
		FaceletHandler local3 = mockFaceletHandler("local3", Type.VARIABLE_DECLARATION);
		FaceletHandler nextHandler = new CompositeFaceletHandler(new FaceletHandler[] { global1, global2,
				this.component1, local1, local2, this.component2, local3 });
		given(this.tagConfig.getNextHandler()).willReturn(nextHandler);
		createDecorateAllHandlerAndApply();
		assertThat(this.variableDeclarationHandlers1.getValue(),
				is(equalTo(Arrays.asList(global1, global2, local1, local2))));
		assertThat(this.variableDeclarationHandlers2.getValue(), is(equalTo(Arrays.asList(global1, global2, local3))));
	}

	@Test
	public void shouldWrapIOException() throws Exception {
		willThrow(new IOException()).given(this.decoratedComponent1).apply(this.ctx, this.parent, TEMPLATE);
		given(this.tagConfig.getNextHandler()).willReturn(this.component1);
		this.thrown.expect(TagAttributeException.class);
		this.thrown.expectMessage("Invalid path : template.xhtml");
		createDecorateAllHandlerAndApply();

	}

	private void createDecorateAllHandlerAndApply() throws IOException {
		this.decorateAllHandler = new MockedDecorateAllHandler(this.tagConfig);
		this.decorateAllHandler.apply(this.ctx, this.parent);
	}

	private FaceletHandler mockFaceletHandler(String name, Type type) {
		FaceletHandler handler = mock(FaceletHandler.class, name);
		given(this.delegate.getType(handler)).willReturn(type);
		return handler;
	}

	private class MockedDecorateAllHandler extends DecorateAllHandler {

		public MockedDecorateAllHandler(TagConfig config) {
			super(config);
		}

		@Override
		protected Delegate getDelegate() {
			return DecorateAllHandlerTest.this.delegate;
		}
	}

}
