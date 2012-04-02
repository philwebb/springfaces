package org.springframework.springfaces.template.facelets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletHandler;

import org.springframework.springfaces.template.facelets.DecorateAllHandler.ChildHandler;

import com.sun.faces.facelets.FaceletContextImplBase;
import com.sun.faces.facelets.TemplateClient;
import com.sun.faces.facelets.el.VariableMapperWrapper;
import com.sun.faces.facelets.tag.ui.DefineHandler;
import com.sun.faces.facelets.tag.ui.ParamHandler;

/**
 * A {@link DecorateAllHandler.ChildHandler} for Mojarra.
 * 
 * @see DecorateAllHandler
 * @author Phillip Webb
 */
class MojarraDecorateAllChildHandler implements ChildHandler {

	private FaceletHandler handler;
	private Map<Object, DefineHandler> defineHandlers = new HashMap<Object, DefineHandler>();
	private List<ParamHandler> paramHandlers = new ArrayList<ParamHandler>();

	public MojarraDecorateAllChildHandler(FaceletHandler handler) {
		this.handler = handler;
	}

	public boolean canAdd(FaceletHandler handler) {
		return (handler instanceof DefineHandler) || (handler instanceof ParamHandler);
	}

	public void add(FaceletHandler handler) {
		if (handler instanceof DefineHandler) {
			addDefineHandler((DefineHandler) handler);
		}
		if (handler instanceof ParamHandler) {
			addParamHandler((ParamHandler) handler);
		}
	}

	private void addDefineHandler(DefineHandler handler) {
		this.defineHandlers.put(handler.getName(), handler);
	}

	private void addParamHandler(ParamHandler faceletHandler) {
		this.paramHandlers.add(faceletHandler);
	}

	public void apply(FaceletContext ctx, final UIComponent parent, String templatePath) throws IOException {
		FaceletContextImplBase faceletContextImpl = (FaceletContextImplBase) ctx;
		VariableMapper originalVariableMapper = ctx.getVariableMapper();
		if (this.paramHandlers != null) {
			ctx.setVariableMapper(new VariableMapperWrapper(originalVariableMapper));
			for (ParamHandler paramHandler : this.paramHandlers) {
				paramHandler.apply(ctx, parent);
			}
		}
		TemplateClient client = new TemplateClient() {
			public boolean apply(FaceletContext ctx, UIComponent parent, String name) throws IOException {
				return applyTemplate(ctx, parent, name);
			}
		};
		faceletContextImpl.pushClient(client);
		try {
			ctx.includeFacelet(parent, templatePath);
		} finally {
			faceletContextImpl.popClient(client);
			faceletContextImpl.setVariableMapper(originalVariableMapper);
		}
	}

	private boolean applyTemplate(FaceletContext ctx, UIComponent parent, String name) throws IOException {
		if (name == null) {
			this.handler.apply(ctx, parent);
			return true;
		}
		DefineHandler definedHandler = this.defineHandlers.get(name);
		if (definedHandler != null) {
			definedHandler.applyDefinition(ctx, parent);
			return true;
		}
		return false;
	}

}