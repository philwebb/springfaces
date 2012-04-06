package org.springframework.springfaces.template.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletHandler;

import org.springframework.springfaces.template.ui.DecorateAllHandler.DecoratedChild;
import org.springframework.springfaces.template.ui.DecorateAllHandler.Type;

import com.sun.faces.facelets.FaceletContextImplBase;
import com.sun.faces.facelets.TemplateClient;
import com.sun.faces.facelets.el.VariableMapperWrapper;
import com.sun.faces.facelets.tag.ui.DefineHandler;
import com.sun.faces.facelets.tag.ui.ParamHandler;

public class MojarraDecorateAllHandlerDelegate implements DecorateAllHandler.Delegate {

	public Type getType(FaceletHandler child) {
		if (child instanceof ParamHandler || child instanceof DefineHandler) {
			return Type.VARIABLE_DECLARATION;
		}
		if (child instanceof ComponentHandler) {
			return Type.COMPONENT;
		}
		return Type.OTHER;
	}

	public DecoratedChild createdDecoratedChild(FaceletHandler handler, List<FaceletHandler> variableDeclarationHandlers) {
		return new MojarraDecoratedChild(handler, variableDeclarationHandlers);
	}

	private static class MojarraDecoratedChild implements DecoratedChild {

		private FaceletHandler handler;

		private List<ParamHandler> paramHandlers = new ArrayList<ParamHandler>();

		private Map<String, DefineHandler> defineHandlers = new HashMap<String, DefineHandler>();

		public MojarraDecoratedChild(FaceletHandler handler, List<FaceletHandler> variableDeclarationHandlers) {
			this.handler = handler;
			for (FaceletHandler variableDeclaration : variableDeclarationHandlers) {
				if (variableDeclaration instanceof ParamHandler) {
					this.paramHandlers.add((ParamHandler) variableDeclaration);
				}
				if (variableDeclaration instanceof DefineHandler) {
					DefineHandler defineHandler = (DefineHandler) variableDeclaration;
					this.defineHandlers.put(defineHandler.getName(), defineHandler);
				}
			}
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
}
