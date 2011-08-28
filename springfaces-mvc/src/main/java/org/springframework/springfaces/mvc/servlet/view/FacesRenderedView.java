package org.springframework.springfaces.mvc.servlet.view;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.web.servlet.View;

//FIXME DC
public interface FacesRenderedView extends View {

	void render(Map<String, ?> model, FacesContext facesContext) throws Exception;

}
