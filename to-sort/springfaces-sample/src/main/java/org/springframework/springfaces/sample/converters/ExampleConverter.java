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
package org.springframework.springfaces.sample.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("exampleconverter")
public class ExampleConverter implements Converter {// , Serializable {// , PartialStateHolder {

	private boolean isTransient;
	private boolean markInitialState;
	private String wibble = "wibble";

	public ExampleConverter() {
		new Exception().printStackTrace();
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value.toUpperCase();
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString().toLowerCase();
	}

	public String getWibble() {
		return wibble;
	}

	public void setWibble(String wibble) {
		clearInitialState();
		this.wibble = wibble;
	}

	public Object saveState(FacesContext context) {
		return wibble;
	}

	public void restoreState(FacesContext context, Object state) {
		this.wibble = (String) state;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean newTransientValue) {
		this.isTransient = newTransientValue;
	}

	public void markInitialState() {
		this.markInitialState = true;
	}

	public boolean initialStateMarked() {
		return markInitialState;
	}

	public void clearInitialState() {
		markInitialState = false;
	}
}
