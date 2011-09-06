package org.springframework.springfaces.convert;

import javax.faces.application.Application;
import javax.faces.convert.Converter;

/**
 * Interface that can be used in conjunction with {@link Converter} or {@link ConverterForClass} beans to indicate the
 * class types that the converter is for. The {@link Application#createConverter(Class)} method will consult the
 * {@link #isForClass(Class)} method to determine when the converter can be used. This interface allows for more
 * advanced converter bindings than standard JSF, for example, by converters could be bound by inspecting class
 * annotations.
 * 
 * @author Phillip Webb
 */
public interface ConditionalConverterForClass {

	/**
	 * Determine if the converter is for the given target class.
	 * @param targetClass the target class
	 * @return if the converter is for the given target class
	 */
	boolean isForClass(Class<?> targetClass);

}
