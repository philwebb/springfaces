package org.springframework.springfaces.sample.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PagedRepository {

	private static final long TOTAL_SIZE = 150;

	public Page<String> someServiceCall(Pageable pageable) {
		List<String> content = new ArrayList<String>();
		for (int i = 0; i < pageable.getPageSize(); i++) {
			int index = pageable.getOffset() + i;
			if (index < TOTAL_SIZE) {
				content.add("Data " + (index + 1));
			}
		}
		return new PageImpl<String>(content, pageable, TOTAL_SIZE);
	}

}
