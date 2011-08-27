package org.springframework.springfaces.mvc.servlet;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.WebApplicationObjectSupport;

public class FacesResourceRequestHandler extends WebApplicationObjectSupport implements HttpRequestHandler {

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance(false);
		if (springFacesContext != null) {
			FacesContext facesContext = springFacesContext.getFacesContext();
			try {
				ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
				resourceHandler.handleResourceRequest(facesContext);
			} finally {
				facesContext.release();
			}
		}
	}

}
