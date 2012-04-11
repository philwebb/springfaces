package org.springframework.springfaces.template.ui;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.Location;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributes;
import javax.faces.view.facelets.TagConfig;

import org.apache.myfaces.view.facelets.AbstractFaceletContext;
import org.apache.myfaces.view.facelets.TemplateClient;
import org.apache.myfaces.view.facelets.el.VariableMapperWrapper;
import org.apache.myfaces.view.facelets.tag.TagAttributesImpl;
import org.apache.myfaces.view.facelets.tag.ui.DefineHandler;
import org.apache.myfaces.view.facelets.tag.ui.ParamHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.template.ui.DecorateAllHandler.DecoratedChild;

/**
 * Tests for {@link MojarraDecorateAllHandlerDelegate}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MyFacesDecorateAllHandlerDelegateTest {

	@Mock
	private AbstractFaceletContext ctx;

	@Mock
	private UIComponent parent;

	@Mock
	private FaceletHandler innerDefineHandler;

	@Captor
	private ArgumentCaptor<TemplateClient> templateClient;

	@Captor
	private ArgumentCaptor<VariableMapper> variableMapper;

	private MyFacesDecorateAllHandlerDelegate delegate = new MyFacesDecorateAllHandlerDelegate();

	@Test
	public void shouldDetectParamHandlerType() throws Exception {
		assertThat(this.delegate.getType(mock(ParamHandler.class)), is(DecorateAllHandler.Type.VARIABLE_DECLARATION));
	}

	@Test
	public void shouldDetectDefineHandlerType() throws Exception {
		assertThat(this.delegate.getType(mockDefineHandler()), is(DecorateAllHandler.Type.VARIABLE_DECLARATION));
	}

	@Test
	public void shouldDetectComponentHandlerType() throws Exception {
		assertThat(this.delegate.getType(mock(ComponentHandler.class)), is(DecorateAllHandler.Type.COMPONENT));
	}

	@Test
	public void shouldDetectOtherHandlerType() throws Exception {
		assertThat(this.delegate.getType(mock(FaceletHandler.class)), is(DecorateAllHandler.Type.OTHER));
	}

	@Test
	public void shouldApply() throws Exception {
		FaceletHandler handler = mock(ComponentHandler.class);
		List<FaceletHandler> variableDeclarationHandlers = new ArrayList<FaceletHandler>();
		DecoratedChild decorated = this.delegate.createdDecoratedChild(handler, variableDeclarationHandlers);
		decorated.apply(this.ctx, this.parent, "template");
		verify(this.ctx).pushClient(this.templateClient.capture());
		verify(this.ctx).includeFacelet(this.parent, "template");
		this.templateClient.getValue().apply(this.ctx, this.parent, null);
		verify(handler).apply(this.ctx, this.parent);
		verify(this.ctx).popClient(this.templateClient.getValue());
	}

	@Test
	public void shouldApplyDefine() throws Exception {
		FaceletHandler handler = mock(ComponentHandler.class);
		List<FaceletHandler> variableDeclarationHandlers = new ArrayList<FaceletHandler>();
		variableDeclarationHandlers.add(mockDefineHandler());
		DecoratedChild decorated = this.delegate.createdDecoratedChild(handler, variableDeclarationHandlers);
		decorated.apply(this.ctx, this.parent, "template");
		verify(this.ctx).pushClient(this.templateClient.capture());
		this.templateClient.getValue().apply(this.ctx, this.parent, "defineName");
		verify(this.innerDefineHandler).apply(this.ctx, this.parent);
	}

	@Test
	public void shouldApplyParameters() throws Exception {
		FaceletHandler handler = mock(ComponentHandler.class);
		List<FaceletHandler> variableDeclarationHandlers = new ArrayList<FaceletHandler>();
		FaceletHandler paramHandler = mock(ParamHandler.class);
		variableDeclarationHandlers.add(paramHandler);
		VariableMapper originalVariableMapper = mock(VariableMapper.class);
		given(this.ctx.getVariableMapper()).willReturn(originalVariableMapper);
		DecoratedChild decorated = this.delegate.createdDecoratedChild(handler, variableDeclarationHandlers);
		decorated.apply(this.ctx, this.parent, null);
		verify(this.ctx, times(2)).setVariableMapper(this.variableMapper.capture());
		assertThat(this.variableMapper.getAllValues().get(0), is(VariableMapperWrapper.class));
		assertThat(this.variableMapper.getAllValues().get(1), is(sameInstance(originalVariableMapper)));
		verify(paramHandler).apply(this.ctx, this.parent);
	}

	private DefineHandler mockDefineHandler() {
		TagConfig tagConfig = mock(TagConfig.class);
		TagAttribute nameAttribute = mock(TagAttribute.class);
		given(nameAttribute.getLocalName()).willReturn("name");
		given(nameAttribute.getNamespace()).willReturn("");
		given(nameAttribute.isLiteral()).willReturn(true);
		given(nameAttribute.getValue()).willReturn("defineName");
		TagAttributes attributes = new TagAttributesImpl(new TagAttribute[] { nameAttribute });
		Tag tag = new Tag(new Location("", 0, 0), "", "", "", attributes);
		given(tagConfig.getTag()).willReturn(tag);
		given(tagConfig.getNextHandler()).willReturn(this.innerDefineHandler);
		DefineHandler defineHandler = new DefineHandler(tagConfig);
		return defineHandler;
	}

}
