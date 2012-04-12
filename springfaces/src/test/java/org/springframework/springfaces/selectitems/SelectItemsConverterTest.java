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
package org.springframework.springfaces.selectitems;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
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
		this.children = new ArrayList<UIComponent>();
		given(this.component.getChildren()).willReturn(this.children);
		this.converter = new TestSelectItemsConverter();
	}

	@Test
	public void shouldGetAsObjectUsingStringValue() throws Exception {
		this.children.add(newSelectItem(1));
		this.children.add(newSelectItem(2));
		this.children.add(newSelectItem(3));
		Object object = this.converter.getAsObject(this.context, this.component, "2");
		assertThat(object, is((Object) 2));
	}

	@Test
	public void shouldFailIfMultipleSelectItemsHaveSameStringValue() throws Exception {
		this.children.add(newSelectItem(1));
		this.children.add(newSelectItem(2));
		this.children.add(newSelectItem(2));
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage("Multiple select items mapped to string value '2' ensure that getAsString always returns a unique value");
		this.converter.getAsObject(this.context, this.component, "2");
	}

	@Test
	public void shouldFailIfNoSelectItemHasStringValue() throws Exception {
		this.children.add(newSelectItem(1));
		this.children.add(newSelectItem(3));
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage("No select item mapped to string value '2' ensure that getAsString always returns a consistent value");
		this.converter.getAsObject(this.context, this.component, "2");
	}

	@Test
	public void shouldSupportSelectItemWithNullValue() throws Exception {
		this.children.add(newSelectItem(1));
		this.children.add(newSelectItem(null));
		this.children.add(newSelectItem(3));
		Object object = this.converter.getAsObject(this.context, this.component, "");
		assertThat(object, is(nullValue()));
	}

	private UIComponent newSelectItem(Integer value) {
		UISelectItem uiSelectItem = new UISelectItem();
		SelectItem selectItem = new SelectItem(value);
		uiSelectItem.setValue(selectItem);
		return uiSelectItem;
	}

	private static class TestSelectItemsConverter extends SelectItemsConverter {
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			return value == null ? "" : value.toString();
		}
	}

}
