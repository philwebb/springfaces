package org.springframework.webflow.samples.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class MyBean {

	private BookingService bookingService;

	@Value("#{myManagedBean}")
	private MyManagedBean myManagedBean;

	public MyBean() {
		System.out.println("test");
	}

	@Autowired
	public void setBookingService(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	public void setSomeValue() {

	}

	@Override
	public String toString() {
		return "hello " + bookingService + " " + myManagedBean;
	}

}
