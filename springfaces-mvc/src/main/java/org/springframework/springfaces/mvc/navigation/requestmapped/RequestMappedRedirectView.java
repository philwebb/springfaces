package org.springframework.springfaces.mvc.navigation.requestmapped;

import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.mvc.bind.ReverseDataBinder;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableRedirectView;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableView;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.util.UriTemplate;

/**
 * A {@link BookmarkableView} that redirects to a URL built dynamically from {@link RequestMapping} annotated
 * {@link Controller} methods. URLs are built by inspecting values of the {@link RequestMapping} annotation along with
 * any method parameters.
 * <p>
 * For example, given the following controller:
 * 
 * <pre>
 * @RequestMapping('/hotel')
 * public class HotelsController {
 *   @RequestMapping('/search')
 *   public void search(String s) {
 *     //...
 *   }
 * }
 * </pre>
 * 
 * A <tt>RequestMappedRedirectView</tt> for the <tt>search</tt> method would create the URL
 * <tt>/springdispatch/hotel/search?s=spring+jsf</tt>.
 * <p>
 * Method parameters are resolved against the <tt>model</tt>, in the example above the model contains the entry
 * <tt>s="spring jsf"</tt>. As well as simple data types, method parameters can also reference any object that
 * {@link DataBinder} supports. The model will also be referenced when resolving URI path template variables (for
 * example <tt>/show/{id}</tt>).
 * <p>
 * There are several limitations to the types of methods that can be used with this view, namely:
 * <ul>
 * <li>The {@link RequestMapping} must contain only a single <tt>value</tt></li>
 * <li>Paths should not contain wildcards (<tt>"*"</tt>, <tt>"?"</tt>, etc)</li>
 * <li>Custom {@link InitBinder} annotationed methods of the controller will not be called</li>
 * </ul>
 * 
 * 
 * @see RequestMappedRedirectDestinationViewResolver
 * @see RequestMappedRedirectViewContext
 * @see ReverseDataBinder
 * 
 * @author Phillip Webb
 */
public class RequestMappedRedirectView implements BookmarkableView {

	/**
	 * Context for the view
	 */
	private RequestMappedRedirectViewContext context;

	/**
	 * The MVC handler being referenced
	 */
	private Object handler;

	/**
	 * The MVC handler method being referenced
	 */
	private Method handlerMethod;

	/**
	 * The model builder.
	 */
	private RequestMappedRedirectViewModelBuilder modelBuilder;

	/**
	 * Create a new {@link RequestMappedRedirectView}.
	 * @param context the context for redirect view
	 * @param handler the MVC handler
	 * @param handlerMethod the MVC handler method that should be used to generate the redirect URL
	 */
	public RequestMappedRedirectView(RequestMappedRedirectViewContext context, Object handler, Method handlerMethod) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(handler, "Handler must not be null");
		Assert.notNull(handlerMethod, "HandlerMethod must not be null");
		this.context = context;
		this.handler = handler;
		this.handlerMethod = BridgeMethodResolver.findBridgedMethod(handlerMethod);
		this.modelBuilder = new RequestMappedRedirectViewModelBuilder(context, handlerMethod);
	}

	public String getContentType() {
		return AbstractView.DEFAULT_CONTENT_TYPE;
	}

	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		NativeWebRequest webRequest = new FacesWebRequest(FacesContext.getCurrentInstance());
		String url = buildRedirectUrl(request);
		Map<String, ?> relevantModel = getRelevantModel(webRequest, url, model);
		createDelegateRedirector(url).render(relevantModel, request, response);
	}

	public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws Exception {
		NativeWebRequest webRequest = new FacesWebRequest(FacesContext.getCurrentInstance());
		String url = buildRedirectUrl(request);
		Map<String, ?> relevantModel = getRelevantModel(webRequest, url, model);
		return createDelegateRedirector(url).getBookmarkUrl(relevantModel, request);
	}

	/**
	 * Build the redirect URL
	 * @param request the HTTP servlet request
	 * @return a redirect URL
	 */
	private String buildRedirectUrl(HttpServletRequest request) {
		RequestMapping methodRequestMapping = AnnotationUtils.findAnnotation(handlerMethod, RequestMapping.class);
		RequestMapping typeLevelRequestMapping = AnnotationUtils.findAnnotation(handler.getClass(),
				RequestMapping.class);
		Assert.state(methodRequestMapping != null, "The handler method must declare @RequestMapping annotation");
		Assert.state(methodRequestMapping.value().length == 1,
				"@RequestMapping must have a single value to be mapped to a URL");
		Assert.state(typeLevelRequestMapping == null || typeLevelRequestMapping.value().length == 1,
				"@RequestMapping on handler class must have a single value to be mapped to a URL");
		String url = context.getDispatcherServletPath();
		if (url == null) {
			url = request.getServletPath();
		}
		if (typeLevelRequestMapping != null) {
			url += typeLevelRequestMapping.value()[0];
		}

		PathMatcher pathMatcher = context.getPathMatcher();
		if (pathMatcher == null) {
			pathMatcher = new AntPathMatcher();
		}
		url = pathMatcher.combine(url, methodRequestMapping.value()[0]);
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		return url;
	}

	/**
	 * Factory method that creates a {@link BookmarkableView} used as a delegate to perform the actual
	 * bookmark/redirect. The default implementation returns a {@link BookmarkableRedirectView}.
	 * @param url the URL that should be redirected to.
	 * @return a {@link BookmarkableView} that will be used to perform the actual bookmark/redirect
	 */
	protected BookmarkableView createDelegateRedirector(String url) {
		return new BookmarkableRedirectView(url, true);
	}

	/**
	 * Extract the relevant items from the source model. The default implementation delegates to
	 * {@link RequestMappedRedirectViewModelBuilder}.
	 * @param request the current request
	 * @param sourceModel the source model
	 * @return relevant model items
	 */
	protected Map<String, ?> getRelevantModel(NativeWebRequest request, String url, Map<String, ?> sourceModel) {
		Map<String, Object> model = modelBuilder.build(request, sourceModel);
		addUriTemplateParameters(model, url, sourceModel);
		return model;
	}

	/**
	 * Add to the model any URI template variable that have not been covered by {@link PathVariable} annotated method
	 * parameters.
	 * @param model the model to add item into
	 * @param url the URL
	 * @param sourceModel the source model
	 */
	private void addUriTemplateParameters(Map<String, Object> model, String url, Map<String, ?> sourceModel) {
		UriTemplate uriTemplate = new UriTemplate(url);
		for (String name : uriTemplate.getVariableNames()) {
			if (!model.containsKey(name)) {
				Assert.state(sourceModel.containsKey(name), "Unable to find URL template variable '" + name
						+ "' in source model");
				model.put(name, sourceModel.get(name));
			}
		}
	}
}