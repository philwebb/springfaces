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

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;

/**
 * Strategy interface that can be used to handle exceptions thrown from JSF.
 * @param <E> The exception type
 * @see SpringFacesExceptionHandlerSupport
 * @author Phillip Webb
 */
@SuppressWarnings("deprecation")
public interface ExceptionHandler<E extends Throwable> {

	/**
	 * Provides the opportunity for the handler to deal with the specified exception.
	 * @param exception the exception to handle. Unlike the the exception contained within the <tt>event</tt> parameter
	 * any JSF exceptions here ({@link FacesException}, {@link ELException} and {@link EvaluationException}) will be
	 * unwrapped.
	 * @param event the event that triggered the exception
	 * @return <tt>true</tt> if the exception is handled by this handler
	 * @throws Exception on error
	 */
	boolean handle(E exception, ExceptionQueuedEvent event) throws Exception;

}
