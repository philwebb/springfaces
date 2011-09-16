/**
 *
 */
package org.springframework.webflow.samples.booking;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("hotels")
@Scope("request")
public class HotelLazyDataModel extends LazyDataModel<Hotel> {

	private static final long serialVersionUID = -8832831134966938627L;

	@Value("#{searchCriteria}")
	SearchCriteria searchCriteria;

	@Autowired
	BookingService bookingService;

	public HotelLazyDataModel() {
		// Setting the page size here prevents divide by zero exceptions. This value
		// is subsequently chanaged to the actual page size by PrimeFaces
		setPageSize(1);
	}

	@Override
	public List<Hotel> load(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters) {
		searchCriteria.setCurrentPage(first / pageSize + 1);
		return bookingService.findHotels(searchCriteria, first, sortField, sortOrder);
	}

	@Override
	public int getRowCount() {
		return bookingService.getNumberOfHotels(searchCriteria);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
}