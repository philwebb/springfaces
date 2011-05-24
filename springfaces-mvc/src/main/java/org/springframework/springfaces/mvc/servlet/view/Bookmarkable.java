package org.springframework.springfaces.mvc.servlet.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Bookmarkable {

	String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws IOException;

}
