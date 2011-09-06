package org.springframework.springfaces.convert;

public interface ConditionalConverterForClass<T> extends ConverterForClass<T> {

	boolean isForClass(Class<?> targetClass);

}
