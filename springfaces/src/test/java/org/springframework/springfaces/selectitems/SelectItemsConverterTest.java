package org.springframework.springfaces.selectitems;

import static org.mockito.BDDMockito.given;

import java.util.ArrayList;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link SelectItemsConverter}.
 * @author Phillip Webb
 */
public class SelectItemsConverterTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private TestSelectItemsConverter converter;

	@Mock
	private FacesContext context;

	@Mock
	private UIComponent component;

	private ArrayList<UIComponent> children;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		children = new ArrayList<UIComponent>();
		given(component.getChildren()).willReturn(children);
		this.converter = new TestSelectItemsConverter();
	}

	@Test
	public void shouldGetAsObjectUsingStringValue() throws Exception {
		children.add(newSelectItem(1));
		children.add(newSelectItem(2));
		children.add(newSelectItem(3));
		converter.getAsObject(context, component, "2");
	}

	@Test
	public void shouldFailIfMultipleSelectItemsHaveSameStringValue() throws Exception {
		children.add(newSelectItem(1));
		children.add(newSelectItem(2));
		children.add(newSelectItem(2));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Multiple select items mapped to string value '2' ensure that getAsString always returns a unique value");
		converter.getAsObject(context, component, "2");
	}

	@Test
	public void shouldFailIfNoSelectItemHasStringValue() throws Exception {
		children.add(newSelectItem(1));
		children.add(newSelectItem(3));
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No select item mapped to string value '2' ensure that getAsString always returns a consistent value");
		converter.getAsObject(context, component, "2");
	}

	private UIComponent newSelectItem(int value) {
		UISelectItem uiSelectItem = new UISelectItem();
		SelectItem selectItem = new SelectItem(value);
		uiSelectItem.setValue(selectItem);
		return uiSelectItem;
	}

	private static class TestSelectItemsConverter extends SelectItemsConverter {
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			return value.toString();
		}
	}

}
