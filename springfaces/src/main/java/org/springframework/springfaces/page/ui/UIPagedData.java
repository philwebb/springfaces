package org.springframework.springfaces.page.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.springframework.springfaces.model.DataModelRowSet;
import org.springframework.springfaces.model.DefaultDataModelRowSet;
import org.springframework.springfaces.model.LazyDataLoader;
import org.springframework.springfaces.page.model.PagedDataModel;
import org.springframework.springfaces.page.model.PagedDataModelState;
import org.springframework.springfaces.page.model.PagedDataRows;
import org.springframework.springfaces.page.model.PrimeFacesPagedDataModel;
import org.springframework.util.Assert;

/**
 * Component that can be used to create a paged {@link DataModel} that lazily fetches data from an underling source. The
 * <tt>value</tt> expression will be called each time new data needs to be fetched and the optional <tt>rowCount</tt>
 * expression will be used to determine the total number of rows. The expression should use the <tt>pageRequest</tt>
 * variable to access {@link PageRequest context} information about the specific data that needs to be returned.
 * <p>
 * For example:
 * 
 * <pre>
 * &lt;s:pagedData value="#{userRepository.findByLastName(backingBean.lastName, pageRequest.offset, pageRequest.pageSize)}"
 *    rowCount="#{userRepository.countByLastName(backingBean.lastName)}"/&gt;
 * 
 * &lt;!-- use the variable pagedData with a scrolled data table --&gt;
 * </pre>
 * <p>
 * The resulting data model is made available as a request scoped variable named '<tt>pagedData</tt>'. You can set a
 * different name using the <tt>var</tt> attribute. The data model will extend the JSF {@link DataModel} class and also
 * implement the {@link PagedDataRows} interface. By default the data model will fetch 10 rows at a time, this can be
 * configured using the <tt>pageSize</tt> attribute.
 * <p>
 * If Spring Data is present on the classpath then <tt>pageRequest</tt> will also implement the
 * <tt>org.springframework.data.domain.Pageable</tt> interface. The <tt>value</tt> expression can also return a
 * <tt>org.springframework.data.domain.Page</tt> removing the need to use <tt>rowCount</tt>.
 * 
 * <pre>
 * &lt;s:pagedData value="#{userRepository.findByLastName(backingBean.lastName, pageRequest)}"/&gt;
 * </pre>
 * <p>
 * If PrimeFaces is present on the classpath then the resulting model will extend
 * <tt>org.primefaces.model.LazyDataModel</tt> rather than <tt>javax.faces.model.DataModel</tt>. Use the
 * {@link PagedDataRows} interface if you need a consistent way of dealing with PrimeFaces and Standard DataModels.
 * 
 * @see PageRequest
 * @see PagedDataRows
 * 
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
	 * @return The variable name
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
	public void restoreState(FacesContext context, Object state) {
		super.restoreState(context, state);
		// Components may need to refer to previous data during decode
		createPagedDataInRequestMap(context);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		createPagedDataInRequestMap(context);
	}

	private void createPagedDataInRequestMap(FacesContext context) {
		Object pagedData = createPagedData();
		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		requestMap.put(getVar(), pagedData);
	}

	/**
	 * Factory method used to create the paged data object to be exposed. By default this method will return a
	 * {@link DataModel} subclass (either {@link PagedDataModel} or {@link PrimeFacesPagedDataModel}).
	 * @return the paged data to expose
	 */
	protected Object createPagedData() {
		LazyDataLoader<Object, PagedDataModelState> lazyDataLoader = new LazyDataLoader<Object, PagedDataModelState>() {
			public DataModelRowSet<Object> getRows(PagedDataModelState state) {
				return UIPagedData.this.getRows(state);
			}
		};
		PagedDataModelState state = (PagedDataModelState) getStateHelper().get(PropertyKeys.dataModelstate);
		if (state == null) {
			state = new PagedDataModelState(getPageSize());
			getStateHelper().put(PropertyKeys.dataModelstate, state);
		}
		return adaptPagedDataModel(new PagedDataModel<Object>(lazyDataLoader, state));
	}

	/**
	 * Strategy method called to adapt a {@link PagedDataModel} to a more appropriate subclass. By default this method
	 * is used to support PrimeFaces.
	 * @param pagedDataModel the data model
	 * @return the adapted model
	 */
	protected Object adaptPagedDataModel(PagedDataModel<Object> pagedDataModel) {
		return primeFacesSupport.wrapPagedDataRows(pagedDataModel);
	}

	/**
	 * Strategy method used to obtain the rows to be used by the {@link PagedDataModel}. By default this method will
	 * expose a <tt>pageRequest</tt> before calling the appropriate EL expressions.
	 * @param stateHolder the state holder
	 * @return the data model rows
	 * @see #getRowCountFromValue(Object)
	 * @see #getContentFromValue(Object)
	 */
	protected DataModelRowSet<Object> getRows(PagedDataModelState stateHolder) {
		Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
		PageRequest pageRequest = createPageRequest(stateHolder);
		Object previousPageRequest = requestMap.put(PAGE_REQUEST_VARIABLE, pageRequest);
		try {
			return executeExpressionsToGetRows(pageRequest);
		} finally {
			// Cleanup the page request
			requestMap.remove(PAGE_REQUEST_VARIABLE);
			if (previousPageRequest != null) {
				requestMap.put(PAGE_REQUEST_VARIABLE, previousPageRequest);
			}
		}
	}

	/**
	 * Create the page request to expose. This method also deals with adding Spring Data <tt>Pageable</tt> support.
	 * @param stateHolder the state holder
	 * @return a page request
	 */
	private PageRequest createPageRequest(PagedDataModelState stateHolder) {
		PageRequest pageRequest = new PageRequestAdapter(stateHolder);
		return springDataSupport.makePageable(pageRequest);
	}

	/**
	 * Executes the appropriate EL expression to obtain page and row count data.
	 * @param pageRequest the page request
	 * @return the data model rows
	 */
	private DataModelRowSet<Object> executeExpressionsToGetRows(PageRequest pageRequest) {
		ELContext context = getFacesContext().getELContext();
		ValueExpression valueExpression = getValue();
		ValueExpression rowCountExpression = getRowCount();
		Object value = valueExpression.getValue(context);
		Object rowCount = (rowCountExpression == null ? null : rowCountExpression.getValue(context));
		return getRowsFromExpressionResults(pageRequest, value, rowCount);
	}

	/**
	 * Obtains row data from the results of the EL expressions.
	 * @param pageRequest the page request
	 * @param value the value EL result
	 * @param rowCount the rowCount EL result
	 * @return the data model rows
	 */
	@SuppressWarnings("unchecked")
	private DataModelRowSet<Object> getRowsFromExpressionResults(PageRequest pageRequest, Object value, Object rowCount) {
		if (rowCount == null) {
			rowCount = getRowCountFromValue(value);
		}
		value = getContentFromValue(value);
		value = value == null ? Collections.emptyList() : value;
		long totalRowCount = -1;
		Assert.isInstanceOf(List.class, value);
		if (rowCount != null) {
			Assert.isInstanceOf(Number.class, rowCount);
			totalRowCount = ((Number) rowCount).longValue();
		}
		return new DefaultDataModelRowSet<Object>(pageRequest.getOffset(), (List<Object>) value,
				pageRequest.getPageSize(), totalRowCount);
	}

	/**
	 * Strategy method used to obtain a count from the value EL result. This method is called when no rowCount EL
	 * expression is specified. By default this method will deal with Spring Data <tt>Page</tt> results.
	 * @param value the value EL result
	 * @return a row count or <tt>null</tt>
	 */
	protected Object getRowCountFromValue(Object value) {
		return springDataSupport.getRowCountFromPage(value);
	}

	/**
	 * Strategy method used to obtain content from the value EL result. By default this method will deal with Spring
	 * Data <tt>Page</tt> results.
	 * @param value the value EL result
	 * @return contents extracted from the value of the original value unchanged.
	 */
	protected Object getContentFromValue(Object value) {
		return springDataSupport.getContentFromPage(value);
	}

	private enum PropertyKeys {
		value, rowCount, var, pageSize, dataModelstate
	}
}
