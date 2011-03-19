package org.springframework.springfaces.mvc.view;

import java.util.List;
import java.util.Map;

public interface Bookmarkable {

	public String getBookmarkURL(Map<String, List<String>> parameters);

}
