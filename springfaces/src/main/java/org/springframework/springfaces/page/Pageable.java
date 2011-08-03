package org.springframework.springfaces.page;

public interface Pageable extends PageContext {

	int getPageNumber();

	int getOffset();

}
