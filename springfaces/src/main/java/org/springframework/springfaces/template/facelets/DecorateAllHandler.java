package org.springframework.springfaces.template.facelets;

import java.io.IOException;
import java.util.LinkedList;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.CompositeFaceletHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributeException;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 * A {@link TagHandler} to support the <tt>s:decorateAll</tt> tag. Provides a drop-in replacement for
 * <tt>ui:decorate</tt> that apply decoration against all children. Nested <tt>ui:param</tt> and <tt>ui:declare</tt>
 * will be grouped with the component immediately above the element. This tag is particularly useful when writing HTML
 * form templates.
 * 
 * @see MojarraDecorateAllChildHandler
 * @see MyFacesDecorateAllChildHandler
 * 
 * @author Phillip Webb
 */
public class DecorateAllHandler extends TagHandler {

	/**
	 * The template used to decorate children.
	 */
	private TagAttribute template;

	/**
	 * Child handlers that the decoration will be applied to.
	 */
	private LinkedList<ChildHandler> childHandlers;

	public DecorateAllHandler(TagConfig config) {
		super(config);
		this.template = this.getRequiredAttribute("template");
		this.childHandlers = new LinkedList<ChildHandler>();
		FaceletHandler[] handlers = getHandlers(this.nextHandler);
		for (FaceletHandler handler : handlers) {
			if (this.childHandlers.size() > 0 && this.childHandlers.getLast().canAdd(handler)) {
				this.childHandlers.getLast().add(handler);
			} else {
				this.childHandlers.add(newChildHandler(handler));
			}
		}
	}

	private FaceletHandler[] getHandlers(FaceletHandler handler) {
		if (this.nextHandler instanceof CompositeFaceletHandler) {
			return ((CompositeFaceletHandler) this.nextHandler).getHandlers();
		}
		return new FaceletHandler[] { this.nextHandler };
	}

	private ChildHandler newChildHandler(FaceletHandler handler) {
		return new MojarraDecorateAllChildHandler(handler);
	}

	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
		String templatePath = this.template.getValue(ctx);
		for (ChildHandler childHandler : this.childHandlers) {
			try {
				childHandler.apply(ctx, parent, templatePath);
			} catch (IOException e) {
				throw new TagAttributeException(this.tag, this.template, "Invalid path : " + templatePath);
			}
		}
	}

	/**
	 * Represents a single child handler to be decorated. Allows for JSF specific implementations (MyFaces, Mojarra).
	 */
	static interface ChildHandler {

		/**
		 * Returns if the specified handler should be {@link #add(FaceletHandler) added} to this child handler.
		 * @param handler the handler to consider
		 * @return <tt>true</tt> if the handler should be added to this child or <tt>false</tt> if a new
		 * {@link ChildHandler} should be created.
		 * @see #add(FaceletHandler)
		 */
		boolean canAdd(FaceletHandler handler);

		/**
		 * Add the specified handler to this child. This method will be called only when {@link #canAdd(FaceletHandler)}
		 * returns <tt>true</tt>
		 * @param handler the handler to add
		 * @see #canAdd(FaceletHandler)
		 */
		void add(FaceletHandler handler);

		/**
		 * Apply the decoration to this child.
		 * @param ctx the facelet context
		 * @param parent the parent component
		 * @param templatePath the path of the template used to decorate the child
		 * @throws IOException
		 */
		void apply(FaceletContext ctx, UIComponent parent, String templatePath) throws IOException;
	}

}
