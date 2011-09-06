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
