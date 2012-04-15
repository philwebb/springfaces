/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.showcase.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

public class ExampleExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapped;

	public ExampleExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExceptionHandlerWrapper#handle()
	 */
	@Override
	public void handle() throws FacesException {
		Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
		while (iterator.hasNext()) {
			ExceptionQueuedEvent event = iterator.next();
			Throwable exception = event.getContext().getException();
			if (isExampleException(exception)) {
				FacesContext context = event.getContext().getContext();
				context.addMessage(null, new FacesMessage("Bad news"));
				context.renderResponse();
				iterator.remove();
				System.err.println("***************************************");
				event.getContext().getException().printStackTrace();
			}
		}
		super.handle();
	}

	/**
	 * @param exception
	 * @return
	 */
	private boolean isExampleException(Throwable exception) {
		if (exception == null) {
			return false;
		}
		if (exception instanceof ExampleException) {
			return true;
		}
		return isExampleException(exception.getCause());
	}

}
