package org.springframework.springfaces.page.ui;

import org.springframework.data.domain.Page;
import org.springframework.util.ClassUtils;

abstract class SpringDataSupport {

	public abstract PageRequest makePageable(PageRequest pageRequest);

	public abstract Object getRowCountFromPage(Object value);

	public abstract Object getContentFromPage(Object value);

	private static final boolean hasSpringData = ClassUtils.isPresent("org.springframework.data.domain.Page",
			SpringDataSupport.class.getClassLoader());

	private static SpringDataSupport instance;

	public static SpringDataSupport getInstance() {
		if (instance == null) {
			instance = (hasSpringData ? new HasSpringData() : new NoSpringData());
		}
		return instance;
	}

	@SuppressWarnings("rawtypes")
	private static class HasSpringData extends SpringDataSupport {
		@Override
		public PageRequest makePageable(PageRequest pageRequest) {
			return new PageablePageRequest(pageRequest);
		}

		@Override
		public Object getRowCountFromPage(Object value) {
			if (value instanceof Page) {
				return ((Page) value).getTotalElements();
			}
			return value;
		}

		@Override
		public Object getContentFromPage(Object value) {
			if (value instanceof Page) {
				return ((Page) value).getContent();
			}
			return value;
		}
	}

	private static class NoSpringData extends SpringDataSupport {
		@Override
		public PageRequest makePageable(PageRequest pageRequest) {
			return pageRequest;
		}

		@Override
		public Object getRowCountFromPage(Object value) {
			return value;
		}

		@Override
		public Object getContentFromPage(Object value) {
			return value;
		}
	}

}
