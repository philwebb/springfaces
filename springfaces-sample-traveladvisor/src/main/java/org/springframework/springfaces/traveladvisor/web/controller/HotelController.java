package org.springframework.springfaces.traveladvisor.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.springfaces.traveladvisor.service.HotelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HotelController {

	private HotelService hotelService;

	@RequestMapping("/hotel/{key}")
	public String hotel(@PathVariable String key, Model model) {
		model.addAttribute(hotelService.getHotel(key));
		return "hotel";
	}

	@RequestMapping("/hotel/{key}/review/{reviewNumber}")
	public String hotelReview(@PathVariable String key, @PathVariable int reviewNumber, Model model) {
		Hotel hotel = hotelService.getHotel(key);
		Review review = hotelService.getReview(hotel, reviewNumber);
		model.addAttribute(review);
		return "review";
	}

	@RequestMapping("/hotel/{key}/write-review")
	public String writeHotelReview(@PathVariable String key, Model model) {
		// FIXME is it wise that we can get here from a bookmark. Back button issues?
		Hotel hotel = hotelService.getHotel(key);
		Review review = new Review();
		model.addAttribute(hotel).addAttribute(review);
		return "writeReview";
	}

	@NavigationMapping
	public NavigationOutcome onSubmitReview(Hotel hotel, Review review) {
		review = hotelService.addReview(hotel, review);
		// FIXME put something in flash scope to be displayed on hotel screen
		return new NavigationOutcome("@hotel", "key", hotel.getKey());
	}

	@Autowired
	public void setHotelService(HotelService hotelService) {
		this.hotelService = hotelService;
	}

}
