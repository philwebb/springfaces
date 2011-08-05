package org.springframework.springfaces.page.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.model.PagedDataRows;
import org.springframework.springfaces.page.model.DefaultPagedDataModelPage;
import org.springframework.springfaces.page.model.DefaultPagedDataModelStateHolder;
import org.springframework.springfaces.page.model.PagedDataModel;
import org.springframework.springfaces.page.model.PagedDataModelPage;
import org.springframework.springfaces.page.model.PagedDataModelPageProvider;
import org.springframework.springfaces.page.model.PagedDataModelStateHolder;
import org.springframework.util.Assert;

/**
 * @see PageRequest
 * @see PagedDataRows
 * @author Phillip Webb
 */
public class UIPagedData extends UIComponentBase {

	public static final String COMPONENT_FAMILY = "spring.faces.PagedData";

	private static final String DEFAULT_VAR = "pagedData";
	private static final Object DEFAULT_PAGE_SIZE = 10;
	private static final String PAGE_REQUEST_VARIABLE = "pageRequest";

	private static PrimeFacesSupport primeFacesSupport = PrimeFacesSupport.getInstance();
	private static SpringDataSupport springDataSupport = SpringDataSupport.getInstance();

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	/**
	 * Return the request-scope attribute under which the {@link PagedDataModel} will be exposed. This property is
	 * <b>not</b> enabled for value binding expressions.
	 */
	public String getVar() {
		String var = (String) getStateHelper().get(PropertyKeys.var);
		return var == null ? DEFAULT_VAR : var;
	}

	/**
	 * Set the request-scope attribute under which the {@link PagedDataModel} will be exposed.
	 * @param var The new request-scope attribute name
	 */
	public void setVar(String var) {
		getStateHelper().put(PropertyKeys.var, var);
	}

	/**
	 * Returns the expression used to obtain a page of data. This expression can be called many times as
	 * {@link PagedDataRows} are navigated. The resulting expression should return a List of rows or, if Spring Data is
	 * being used a <tt>Page</tt> object can also be returned.
	 * @return the {@link ValueExpression} to obtain the page data
	 */
	protected ValueExpression getValue() {
		ValueExpression value = getValueExpression(PropertyKeys.value.toString());
		Assert.notNull(value, "UIPageData components must include a value attribute");
		return value;
	}

	/**
	 * Returns the optional expression used to obtain the total row count. This expression can be called many times as
	 * {@link PagedDataRows} are navigated. The resulting expression should return an int or long value.
	 * @return the {@link ValueExpression} to obtain the number of rows
	 */
	protected ValueExpression getRowCount() {
		return getValueExpression(PropertyKeys.rowCount.toString());
	}

	/**
	 * Return the initial page size for the {@link PagedDataRows}. If not specified the default value of 10 is used.
	 * @return the page size
	 */
	public int getPageSize() {
		return (Integer) getStateHelper().eval(PropertyKeys.pageSize, DEFAULT_PAGE_SIZE);
	}

	/**
	 * Set the initial page size for the {@link PagedDataRows}.
	 * @param pageSize the page size
	 */
	public void setPageSize(int pageSize) {
		Assert.isTrue(pageSize > 0, "PageSize must be a positive number");
		getStateHelper().put(PropertyKeys.pageSize, pageSize);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		PagedDataModelPageProvider<Object> pageProvider = new PagedDataModelPageProvider<Object>() {
			public PagedDataModelPage<Object> getPage(PagedDataModelStateHolder stateHolder) {
				return UIPagedData.this.setupPageRequestAndGetPage(stateHolder);
			}
		};
		PagedDataModelStateHolder stateHolder = (PagedDataModelStateHolder) getStateHelper().get(
				PropertyKeys.stateHolder);
		if (stateHolder == null) {
			stateHolder = new DefaultPagedDataModelStateHolder(getPageSize());
			getStateHelper().put(PropertyKeys.stateHolder, stateHolder);
		}
		PagedDataRows<Object> dataModel = primeFacesSupport.wrapPagedDataRows(new PagedDataModel<Object>(pageProvider,
				stateHolder));
		Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
		requestMap.put(getVar(), dataModel);
	}

	protected PagedDataModelPage<Object> setupPageRequestAndGetPage(PagedDataModelStateHolder stateHolder) {
		Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
		// Setup the page request so that EL expression can access it
		PageRequest pageRequest = new StateHolderPageRequestAdapter(stateHolder);
		pageRequest = springDataSupport.makePageable(pageRequest);
		Object previousPageRequest = requestMap.put(PAGE_REQUEST_VARIABLE, pageRequest);
		try {
			return getPage(pageRequest);
		} finally {
			// Cleanup the page request
			requestMap.remove(PAGE_REQUEST_VARIABLE);
			if (previousPageRequest != null) {
				requestMap.put(PAGE_REQUEST_VARIABLE, previousPageRequest);
			}
		}
	}

	private PagedDataModelPage<Object> getPage(PageRequest pageRequest) {
		ELContext context = getFacesContext().getELContext();
		ValueExpression valueExpression = getValue();
		ValueExpression rowCountExpression = getRowCount();
		Object value = valueExpression.getValue(context);
		Object rowCount = (rowCountExpression == null ? null : rowCountExpression.getValue(context));
		return newPage(pageRequest, value, rowCount);
	}

	@SuppressWarnings("unchecked")
	private PagedDataModelPage<Object> newPage(PageRequest pageRequest, Object value, Object rowCount) {
		if (rowCount == null) {
			rowCount = springDataSupport.getRowCountFromPage(value);
		}
		value = springDataSupport.getContentFromPage(value);
		value = value == null ? Collections.emptyList() : value;
		long totalRowCount = -1;
		Assert.isInstanceOf(List.class, value);
		if (rowCount != null) {
			Assert.isInstanceOf(Number.class, rowCount);
			totalRowCount = ((Number) rowCount).longValue();
		}
		return new DefaultPagedDataModelPage<Object>(pageRequest, (List<Object>) value, totalRowCount);
	}

	private enum PropertyKeys {
		value, rowCount, var, pageSize, stateHolder
	}
}
