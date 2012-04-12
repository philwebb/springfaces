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
package org.springframework.springfaces.mvc.render;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

public class ClientFacesViewStateHandler implements FacesViewStateHandler {

	private static final String ID = "org.springframework.springfaces.id";

	public void write(FacesContext facesContext, ViewArtifact viewState) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		writeHiddenInput(writer, ID, viewState.toString());
	}

	private void writeHiddenInput(ResponseWriter writer, String id, String value) throws IOException {
		// FIXME we should escape data
		// FIXME we perhaps should encrypt or sign
		writer.write("<input type=\"hidden\" name=\"");
		writer.write(id);
		writer.write("\" id=\"");
		writer.write(id);
		writer.write("\" value=\"");
		writer.write(value);
		writer.write("\"\\>");
	}

	public ViewArtifact read(HttpServletRequest request) throws IOException {
		String id = request.getParameter(ID);
		if (id == null) {
			return null;
		}
		return new ViewArtifact(id);
	}
}
