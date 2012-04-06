package org.springframework.springfaces.template.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * <tt>ui:decorate</tt> that apply decoration against all child components. Nested <tt>ui:param</tt> and
 * <tt>ui:declare</tt> elements above the first component are global, subsequent elements will be grouped with the
 * component immediately above.
 * <p>
 * This tag is particularly useful when writing HTML form templates.
 * 
 * @see MojarraDecorateAllChildHandler
 * @see MyFacesDecorateAllChildHandler
 * 
 * @author Phillip Webb
 */
public class DecorateAllHandler extends TagHandler {

	private TagAttribute template;

	private List<Object> children;

	public DecorateAllHandler(TagConfig config) {
		super(config);
		Delegate childHandler = getDelegate();
		this.template = this.getRequiredAttribute("template");
		this.children = new ArrayList<Object>();
		List<FaceletHandler> globalVariableDeclarations = new ArrayList<FaceletHandler>();
		List<FaceletHandler> variableDeclarations = new ArrayList<FaceletHandler>();
		FaceletHandler component = null;
		for (FaceletHandler child : getHandlers(this.nextHandler)) {
			Type type = childHandler.getType(child);
			if (type == Type.COMPONENT) {
				if (component != null) {
					this.children.add(childHandler.createdDecoratedChild(component, variableDeclarations));
					variableDeclarations.clear();
					variableDeclarations.addAll(globalVariableDeclarations);
				}
				component = child;
			} else if (type == Type.VARIABLE_DECLARATION) {
				if (component == null) {
					globalVariableDeclarations.add(child);
				} else {
					variableDeclarations.add(child);
				}
			} else {
				this.children.add(child);
			}
		}
		if (component != null) {
			this.children.add(childHandler.createdDecoratedChild(component, variableDeclarations));
		}
	}

	private FaceletHandler[] getHandlers(FaceletHandler handler) {
		if (this.nextHandler instanceof CompositeFaceletHandler) {
			return ((CompositeFaceletHandler) this.nextHandler).getHandlers();
		}
		return new FaceletHandler[] { this.nextHandler };
	}

	private Delegate getDelegate() {
		return new MojarraDecorateAllHandlerDelegate();
	}

	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
		String templatePath = this.template.getValue(ctx);
		for (Object child : this.children) {
			try {
				if (child instanceof DecoratedChild) {
					((DecoratedChild) child).apply(ctx, parent, templatePath);
				} else {
					((FaceletHandler) child).apply(ctx, parent);
				}
			} catch (IOException e) {
				throw new TagAttributeException(this.tag, this.template, "Invalid path : " + templatePath);
			}
		}
	}

	enum Type {
		COMPONENT, VARIABLE_DECLARATION, OTHER
	}

	static interface Delegate {

		Type getType(FaceletHandler child);

		DecoratedChild createdDecoratedChild(FaceletHandler handler, List<FaceletHandler> variableDeclarationHandlers);
	}

	static interface DecoratedChild {
		void apply(FaceletContext ctx, UIComponent parent, String template) throws IOException;
	}

}
