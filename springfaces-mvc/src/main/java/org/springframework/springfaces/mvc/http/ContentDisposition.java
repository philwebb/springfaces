package org.springframework.springfaces.mvc.http;

import java.util.Date;

import org.springframework.core.io.InputStreamSource;

public interface ContentDisposition extends InputStreamSource {

	DispositionType getDispositionType();

	String getFilename();

	Date getCreationDate();

	Date getModificationDate();

	Date getReadDate();

	Long getSize();
}
