package org.springframework.springfaces.mvc.view;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

public class ClientFacesViewStateHandler implements FacesViewStateHandler {

	private static final String ID = "org.springframework.springfaces.id";
	private static final String NAME = "org.springframework.springfaces.name";

	public void writeViewState(FacesContext facesContext, ViewState viewState) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		writeHiddenInput(writer, NAME, viewState.getViewName());
		writeHiddenInput(writer, ID, viewState.getViewId());
	}

	private void writeHiddenInput(ResponseWriter writer, String id, String value) throws IOException {
		//FIXME we should escape data
		//FIXME we perhaps should encrypt or sign
		writer.write("<input type=\"hidden\" name=\"");
		writer.write(id);
		writer.write("\" id=\"");
		writer.write(id);
		writer.write("\" value=\"");
		writer.write(value);
		writer.write("\"\\>");
	}

	public ViewState readViewState(HttpServletRequest request) throws IOException {
		String name = request.getParameter(NAME);
		String id = request.getParameter(ID);
		if (id == null || name == null) {
			return null;
		}
		//Since we are a postback our action URL should be null
		String actionUrl = null;
		return new DefaultViewState(name, id, actionUrl);
	}
}
