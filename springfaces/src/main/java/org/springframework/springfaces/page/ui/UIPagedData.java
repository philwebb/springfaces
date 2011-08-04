package org.springframework.springfaces.page.ui;

import java.io.IOException;

import javax.el.Expression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.model.PagedDataRows;

/**
 * @see PagedDataRows
 * @author Phillip Webb
 */
public class UIPagedData extends UIComponentBase {

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
	 * The total number of rows contained in the model at any one time. Optional defaults to 10.
	 */
	private int pageSize;

	/*
	 * The variable used to expose the datamodel, optional defaults to pagedData
	 */
	private String var;

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		// Create a delegate state holder (delegate to this state)
		// Create a page provider

		// on getPage(pageable)
		// - make pageable spring data pageable if possible
		// - put pageable in request scope
		// - call value expression
		// - call the rowCount expression (if needed)
		// - remove pageable in request scope
		// - create a PageImpl with optional spring data Page as well

		// put into request scope
		// new PagedDataModel<E>(stateHolder, pageProvider);
		// wrap with PrimeFaces if needed
	}

	@Override
	public String getFamily() {
		// TODO Auto-generated method stub
		return null;
	}

}
