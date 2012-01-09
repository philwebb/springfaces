package org.springframework.springfaces.selectitems.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.selectitems.ui.UISelectItems.ExposedUISelectItems;
import org.springframework.springfaces.selectitems.ui.UISelectItems.UISelectItemsConverter;

/**
 * Tests for {@link UISelectItems}.
 * 
 * @author Phillip Webb
 */
public class UISelectItemsTest {

	private UISelectItems selectItems;

	@Captor
	private ArgumentCaptor<Converter> converterCaptor;

	@Mock
	private FacesContext facesContext;

	@Mock
	private ApplicationContext applicationContext;

	private TypeDescriptor typeDescriptor;

	private Locale locale = Locale.GERMANY;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.selectItems = new UISelectItems() {
			@Override
			protected TypeDescriptor getTypeDescriptor(ValueExpression valueExpression, ELContext elContext) {
				return typeDescriptor;
			}
		};
		FacesContextSetter.setCurrentInstance(facesContext);
		SpringFacesMocks.setupSpringFacesIntegration(facesContext, applicationContext);
		Map<String, Object> requestMap = new HashMap<String, Object>();
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		given(facesContext.getExternalContext().getRequestMap()).willReturn(requestMap);
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		given(viewRoot.getLocale()).willReturn(locale);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldGetFamily() throws Exception {
		assertThat(selectItems.getFamily(), is("spring.faces.SelectItems"));
	}

	@Test
	public void shouldAddDelegateSelectItemsComponentOnAttach() throws Exception {
		UIComponent parent = mockParent(false);
		selectItems.setParent(parent);
		assertThat(parent.getChildren().get(0), is(ExposedUISelectItems.class));
	}

	@Test
	public void shouldAddConverterOnAttach() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		verify((EditableValueHolder) parent).setConverter(converterCaptor.capture());
		assertThat(converterCaptor.getValue(), is(UISelectItemsConverter.class));
	}

	@Test
	public void shouldNotReplaceExistingConverterOnAttach() throws Exception {
		UIComponent parent = mockParent(true);
		Converter converter = mock(Converter.class);
		given(((EditableValueHolder) parent).getConverter()).willReturn(converter);
		selectItems.setParent(parent);
		verify((EditableValueHolder) parent, never()).setConverter(any(Converter.class));
	}

	@Test
	public void shouldRemoveDelegateSelectItemsComponentOnDetatch() throws Exception {
		UIComponent parent = mockParent(false);
		selectItems.setParent(parent);
		selectItems.setParent(mockParent(false));
		assertThat(parent.getChildren().size(), is(0));
	}

	@Test
	public void shouldRemoveConverterOnDetatch() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		verify((EditableValueHolder) parent).setConverter(converterCaptor.capture());
		given(((EditableValueHolder) parent).getConverter()).willReturn(converterCaptor.getValue());
		selectItems.setParent(mockParent(true));
		verify((EditableValueHolder) parent).setConverter(null);
	}

	@Test
	public void shouldNotRemoveExistingConverterOnDetatch() throws Exception {
		UIComponent parent = mockParent(true);
		Converter converter = mock(Converter.class);
		given(((EditableValueHolder) parent).getConverter()).willReturn(converter);
		selectItems.setParent(parent);
		selectItems.setParent(mockParent(true));
		verify((EditableValueHolder) parent, never()).setConverter(null);
	}

	@Test
	public void shouldGetSelectItemsFromArray() throws Exception {
		selectItems.setParent(mockParent(true));
		SelectItem selectItem = new SelectItem();
		SelectItem[] values = { selectItem };
		selectItems.setValues(values);
		List<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual, is(equalTo((Collection) Collections.singletonList(selectItem))));
	}

	@Test
	public void shouldGetSelectItemsFromCollection() throws Exception {
		selectItems.setParent(mockParent(true));
		Collection<SelectItem> values = Collections.singleton(new SelectItem());
		selectItems.setValues(values);
		Collection<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual, is(equalTo((Collection) new ArrayList<SelectItem>(values))));
	}

	@Test
	public void shouldGetSelectItemsFromCommaString() throws Exception {
		selectItems.setParent(mockParent(true));
		String values = "1,2,3";
		selectItems.setValues(values);
		List<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual.size(), is(3));
		assertThat(actual.get(0).getLabel(), is("1"));
		assertThat(actual.get(1).getLabel(), is("2"));
		assertThat(actual.get(2).getLabel(), is("3"));
		assertThat(actual.get(0).getValue(), is((Object) "1"));
		assertThat(actual.get(1).getValue(), is((Object) "2"));
		assertThat(actual.get(2).getValue(), is((Object) "3"));
	}

	@Test
	public void shouldDeduceSelectItemsFromEnum() throws Exception {
		UIComponent parent = mockParent(true);
		ValueExpression valueExpression = mock(ValueExpression.class);
		given(parent.getValueExpression("value")).willReturn(valueExpression);
		typeDescriptor = TypeDescriptor.valueOf(SampleEnum.class);
		selectItems.setParent(parent);
		List<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual.size(), is(3));
		assertThat(actual.get(0).getLabel(), is("ONE"));
		assertThat(actual.get(1).getLabel(), is("TWO"));
		assertThat(actual.get(2).getLabel(), is("THREE"));
		assertThat(actual.get(0).getValue(), is((Object) SampleEnum.ONE));
		assertThat(actual.get(1).getValue(), is((Object) SampleEnum.TWO));
		assertThat(actual.get(2).getValue(), is((Object) SampleEnum.THREE));
	}

	@Test
	public void shouldDeduceSelectItemsFromBoolean() throws Exception {
		UIComponent parent = mockParent(true);
		ValueExpression valueExpression = mock(ValueExpression.class);
		given(parent.getValueExpression("value")).willReturn(valueExpression);
		typeDescriptor = TypeDescriptor.valueOf(Boolean.class);
		selectItems.setParent(parent);
		List<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0).getLabel(), is("Yes"));
		assertThat(actual.get(1).getLabel(), is("No"));
		assertThat(actual.get(0).getValue(), is((Object) Boolean.TRUE));
		assertThat(actual.get(1).getValue(), is((Object) Boolean.FALSE));
	}

	@Test
	public void shouldDeduceSelectItemsFromArray() throws Exception {
		UIComponent parent = mockParent(true);
		ValueExpression valueExpression = mock(ValueExpression.class);
		given(parent.getValueExpression("value")).willReturn(valueExpression);
		typeDescriptor = TypeDescriptor.valueOf(SampleEnum[].class);
		selectItems.setParent(parent);
		List<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual.size(), is(3));
		assertThat(actual.get(2).getValue(), is((Object) SampleEnum.THREE));
	}

	@Test
	public void shouldDeduceSelectItemsFromCollection() throws Exception {
		UIComponent parent = mockParent(true);
		ValueExpression valueExpression = mock(ValueExpression.class);
		given(parent.getValueExpression("value")).willReturn(valueExpression);
		typeDescriptor = TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(SampleEnum.class));
		selectItems.setParent(parent);
		List<SelectItem> actual = selectItems.getSelectItems();
		assertThat(actual.size(), is(3));
		assertThat(actual.get(2).getValue(), is((Object) SampleEnum.THREE));
	}

	@Test
	public void shouldUseSensibleDefaultWhenNoAttributes() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.TWO));
		SelectItem actual = selectItems.getSelectItems().get(0);
		assertThat(actual.getLabel(), is("TWO"));
		assertThat(actual.getDescription(), is(nullValue()));
		assertThat(actual.getValue(), is((Object) SampleEnum.TWO));
		assertThat(actual.isDisabled(), is(false));
		assertThat(actual.isEscape(), is(true));
		assertThat(actual.isNoSelectionOption(), is(false));
	}

	@Test
	public void shouldUseAttributesToConvertToSelectItem() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		RunnableAsserts assertItemIsSet = assertTheItemVarIsSet("item", SampleEnum.ONE);
		selectItems.setValueExpression("itemLabel", mockValueExpression(assertItemIsSet, "label"));
		selectItems.setValueExpression("itemDescription", mockValueExpression(assertItemIsSet, "description"));
		selectItems.setValueExpression("itemDisabled", mockValueExpression(assertItemIsSet, true));
		selectItems.setValueExpression("itemEscape", mockValueExpression(assertItemIsSet, false));
		SelectItem actual = selectItems.getSelectItems().get(0);
		assertThat(actual.getLabel(), is("label"));
		assertThat(actual.getDescription(), is("description"));
		assertThat(actual.isDisabled(), is(true));
		assertThat(actual.isEscape(), is(false));
	}

	@Test
	public void shouldUseItemConverterStringValueAttribute() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		RunnableAsserts assertItemIsSet = assertTheItemVarIsSet("item", SampleEnum.ONE);
		selectItems.setValueExpression("itemConverterStringValue", mockValueExpression(assertItemIsSet, "1"));
		selectItems.getSelectItems();
		verify((EditableValueHolder) parent).setConverter(converterCaptor.capture());
		String actual = converterCaptor.getValue().getAsString(facesContext, parent, SampleEnum.ONE);
		assertThat(actual, is("1"));
	}

	@Test
	public void shouldSupportCustomVar() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		selectItems.setVar("example");
		RunnableAsserts assertItemIsSet = assertTheItemVarIsSet("example", SampleEnum.ONE);
		selectItems.setValueExpression("itemLabel", mockValueExpression(assertItemIsSet, "label"));
		selectItems.setValueExpression("itemConverterStringValue", mockValueExpression(assertItemIsSet, "1"));
		selectItems.getSelectItems();
		verify((EditableValueHolder) parent).setConverter(converterCaptor.capture());
		converterCaptor.getValue().getAsString(facesContext, parent, SampleEnum.ONE);
	}

	@Test
	public void shouldUseObjectMessageSourceToCreateLabel() throws Exception {
		ObjectMessageSource messageSource = mock(ObjectMessageSource.class);
		given(applicationContext.containsBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)).willReturn(true);
		given(applicationContext.getBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME))
				.willReturn(messageSource);
		given(messageSource.getMessage(SampleEnum.ONE, null, locale)).willReturn("Eins");
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		SelectItem actual = selectItems.getSelectItems().get(0);
		assertThat(actual.getLabel(), is("Eins"));
	}

	@Test
	public void shouldSupportCustomMessageSource() throws Exception {
		ObjectMessageSource messageSource = mock(ObjectMessageSource.class);
		given(messageSource.getMessage(SampleEnum.ONE, null, locale)).willReturn("Eins");
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		selectItems.setMessageSource(messageSource);
		SelectItem actual = selectItems.getSelectItems().get(0);
		assertThat(actual.getLabel(), is("Eins"));
	}

	@Test
	public void shouldIgnoreNoSuchObjectMessageException() throws Exception {
		ObjectMessageSource messageSource = mock(ObjectMessageSource.class);
		given(messageSource.getMessage(SampleEnum.ONE, null, locale)).willThrow(
				new NoSuchObjectMessageException(SampleEnum.ONE, locale));
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		selectItems.setMessageSource(messageSource);
		SelectItem actual = selectItems.getSelectItems().get(0);
		assertThat(actual.getLabel(), is("ONE"));
	}

	@Test
	public void shouldUseToStringForConverter() throws Exception {
		UIComponent parent = mockParent(true);
		selectItems.setParent(parent);
		selectItems.setValues(Collections.singleton(SampleEnum.ONE));
		selectItems.getSelectItems();
		verify((EditableValueHolder) parent).setConverter(converterCaptor.capture());
		String actual = converterCaptor.getValue().getAsString(facesContext, parent, SampleEnum.ONE);
		assertThat(actual, is("ONE"));
	}

	private ValueExpression mockValueExpression(final RunnableAsserts runnable, final Object value) {
		ValueExpression valueExpression = mock(ValueExpression.class);
		given(valueExpression.getValue(any(ELContext.class))).willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				runnable.run();
				return value;
			}
		});
		return valueExpression;
	}

	private UIComponent mockParent(boolean editableValueHolder) {
		MockSettings settings = withSettings();
		if (editableValueHolder) {
			settings = settings.extraInterfaces(EditableValueHolder.class);
		}
		UIComponent parent = mock(UIComponent.class, settings);
		List<UIComponent> children = new ArrayList<UIComponent>();
		given(parent.getChildren()).willReturn(children);
		return parent;
	}

	private RunnableAsserts assertTheItemVarIsSet(final String variableName, final Object expectedValue) {
		RunnableAsserts assertItemIsSet = new RunnableAsserts() {
			public void run() throws Exception {
				Object item = facesContext.getExternalContext().getRequestMap().get(variableName);
				assertThat(item, is(expectedValue));
			}
		};
		return assertItemIsSet;
	}

	enum SampleEnum {
		ONE, TWO, THREE
	}

	private static interface RunnableAsserts {
		public void run() throws Exception;
	}
}
