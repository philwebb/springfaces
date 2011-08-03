package org.springframework.springfaces.page;

import javax.el.Expression;
import javax.faces.component.UIComponentBase;

public class UIPagedDataModel extends UIComponentBase {

	/*
	 * Expression called to provide a single page of data. This expression will be called multiple times.
	 * 
	 * Can return a List or Spring Data Page During call has access to "page"
	 */
	private Expression value;

	/*
	 * Expression to obtain the total number of rows. This expression will be called multiple times Optional and if not
	 * specified the result with be obtained from value (when it returns a page) or -1
	 */
	private Expression rowCount;

	/*
	 * The total number of rows contained in the model at any one time. Same as UIData.rows Optional defaults to 10
	 */
	private int pageSize;

	/*
	 * The variable used to expose the datamodel, optional defaults to pagedData
	 */
	private String var;

	@Override
	public String getFamily() {
		// TODO Auto-generated method stub
		return null;
	}

}
