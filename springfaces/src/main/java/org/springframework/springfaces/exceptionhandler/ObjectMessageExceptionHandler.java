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
package org.springframework.springfaces.exceptionhandler;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.Ordered;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.message.ObjectMessageSourceUtils;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;

/**
 * {@link ExceptionHandler} that converts exceptions to {@link FacesMessage}s using an {@link ObjectMessageSource}.
 * 
 * @author Phillip Webb
 */
public class ObjectMessageExceptionHandler implements ExceptionHandler<Throwable>, MessageSourceAware, Ordered {

	private int order = -1;

	private ObjectMessageSource messageSource;

	public boolean handle(Throwable exception, ExceptionQueuedEvent event) throws Exception {
		Assert.state(this.messageSource != null, "MessageSource must not be null");
		ExceptionQueuedEventContext eventContext = event.getContext();
		FacesContext facesContext = eventContext.getContext();
		try {
			String message = getMessage(exception, FacesUtils.getLocale(facesContext));
			facesContext.addMessage(null, new FacesMessage(message));
			return true;
		} catch (NoSuchObjectMessageException e) {
			return false;
		}
	}

	protected String getMessage(Throwable exception, Locale locale) {
		return this.messageSource.getMessage(exception, null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = ObjectMessageSourceUtils.getObjectMessageSource(messageSource);
	}

	public int getOrder() {
		return this.order;
	}

	/**
	 * Set the order. By default this class is executed before other {@link ExceptionHandler}s.
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

}
