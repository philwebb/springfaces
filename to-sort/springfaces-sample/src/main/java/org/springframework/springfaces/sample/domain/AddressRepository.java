package org.springframework.springfaces.sample.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.springfaces.page.ui.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.util.comparator.CompoundComparator;

@Repository
public class AddressRepository {

	private static final Random RANDOM = new SecureRandom();

	private static final List<Address> ADDRESSES;

	private static final Map<Address, FilterableAddress> FILTERABLE_ADDRESSES;

	private static final String RCHARS = "abcdefghijklmnopqrstuvwxyz";

	static {
		List<Address> list = new ArrayList<Address>();
		for (int i = 0; i < 155; i++) {
			list.add(newAddress(i));
		}
		ADDRESSES = Collections.unmodifiableList(list);
		FILTERABLE_ADDRESSES = new HashMap<Address, AddressRepository.FilterableAddress>();
		for (Address address : list) {
			FILTERABLE_ADDRESSES.put(address, new FilterableAddress(address));
		}
	}

	private static Address newAddress(int i) {
		return new Address(i, randomAddressLine(), randomAddressLine(), randomPostcode());
	}

	private static String randomAddressLine() {
		return generateString(20);
	}

	private static String randomPostcode() {
		return generateString(3) + " " + generateString(3);
	}

	public static String generateString(int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = RCHARS.charAt(RANDOM.nextInt(RCHARS.length()));
		}
		return new String(text);
	}

	public Page<Address> findAll(Pageable pageable) {
		List<Address> source = new ArrayList<Address>(ADDRESSES);
		if (pageable instanceof PageRequest) {
			filter(source, ((PageRequest) pageable).getFilters());
		}
		if (pageable.getSort() != null) {
			sort(source, pageable.getSort());
		}
		List<Address> content = new ArrayList<Address>();
		for (int i = 0; i < pageable.getPageSize(); i++) {
			int index = pageable.getOffset() + i;
			if (index < source.size()) {
				content.add(source.get(index));
			}
		}
		return new PageImpl<Address>(content, pageable, source.size());
	}

	private void filter(List<Address> source, Map<String, String> filters) {
		if (filters != null && filters.size() > 0) {
			for (Iterator<Address> iterator = source.iterator(); iterator.hasNext();) {
				Address address = (Address) iterator.next();
				FilterableAddress filterableAddress = FILTERABLE_ADDRESSES.get(address);
				if (filterableAddress.isFiltered(filters)) {
					iterator.remove();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void sort(List<Address> source, Sort sort) {
		CompoundComparator<Address> c = new CompoundComparator<Address>();
		for (Sort.Order order : sort) {
			c.addComparator(new PropertyComparator(order.getProperty(), false, order.isAscending()));
		}
		Collections.sort(source, c);
	}

	private static class FilterableAddress {

		private BeanWrapperImpl bean;
		private Map<String, String> propertyValues = new HashMap<String, String>();

		public FilterableAddress(Address address) {
			this.bean = new BeanWrapperImpl(address);
		}

		public boolean isFiltered(Map<String, String> filters) {
			for (Map.Entry<String, String> filter : filters.entrySet()) {
				if (isFilterd(filter.getKey(), filter.getValue())) {
					return true;
				}
			}
			return false;
		}

		private boolean isFilterd(String propertyName, String filter) {
			if (StringUtils.hasLength(filter)) {
				String value = getPropertyValue(propertyName);
				return value.indexOf(filter) == -1;
			}
			return false;
		}

		private String getPropertyValue(String propertyName) {
			String stringValue = propertyValues.get(propertyName);
			if (stringValue == null) {
				Object value = bean.getPropertyValue(propertyName);
				stringValue = (value == null ? "" : String.valueOf(value));
				propertyValues.put(propertyName, stringValue);
			}
			return stringValue;
		}
	}
}
