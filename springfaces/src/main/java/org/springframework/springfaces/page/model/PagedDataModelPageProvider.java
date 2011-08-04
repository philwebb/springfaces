package org.springframework.springfaces.page.model;

import org.springframework.springfaces.page.ui.PageaRequest;

public interface PagedDataModelPageProvider<E> {
	PagedDataModelPage<E> getPage(PageaRequest pageable);
}