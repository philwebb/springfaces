package org.springframework.springfaces.page.ui;

import org.springframework.data.domain.Page;
import org.springframework.util.ClassUtils;

/**
 * Support class that is used to dynamically enhance functionality when Spring Data is available.
 * 
 * @author Phillip Webb
 */
abstract class SpringDataSupport {

	/**
	 * Extend the specified {@link PageRequest} with the Spring Data <tt>Pageable</tt> interface.
	 * @param pageRequest the page request
	 * @return a new page request that also support <tt>Pageable</tt> or the original request if Spring Data is not
	 * available.
	 */
	public abstract PageRequest makePageable(PageRequest pageRequest);

	/**
	 * Extract the row count from the specified value. If the value is a Spring Data <tt>Page</tt> the row count will be
	 * extracted, otherwise the null is returned.
	 * @param value the value
	 * @return the row count or <tt>null</tt>
	 */
	public abstract Object getRowCountFromPage(Object value);

	/**
	 * Extract the content from the specified value. If the value is a Spring Data <tt>Page</tt> the contexts will be
	 * extracted, otherwise the original value is returned.
	 * @param value
	 * @return the contents or the original value
	 */
	public abstract Object getContentFromPage(Object value);

	private static boolean hasSpringData = ClassUtils.isPresent("org.springframework.data.domain.Page",
			SpringDataSupport.class.getClassLoader());

	private static SpringDataSupport instance;

	public static SpringDataSupport getInstance() {
		if (instance == null) {
			instance = (hasSpringData ? new HasSpringData() : new NoSpringData());
		}
		return instance;
	}

	/**
	 * Override if spring data is available. This is primarily to aid testing.
	 * @param hasSpringData if spring data is available.
	 */
	static void setHasSpringData(boolean hasSpringData) {
		SpringDataSupport.hasSpringData = hasSpringData;
		instance = null;
	}

	@SuppressWarnings("rawtypes")
	private static class HasSpringData extends SpringDataSupport {
		@Override
		public PageRequest makePageable(PageRequest pageRequest) {
			return new SpringDataPageRequest(pageRequest);
		}

		@Override
		public Object getRowCountFromPage(Object value) {
			if (value instanceof Page) {
				return ((Page) value).getTotalElements();
			}
			return null;
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
			return null;
		}

		@Override
		public Object getContentFromPage(Object value) {
			return value;
		}
	}
}
