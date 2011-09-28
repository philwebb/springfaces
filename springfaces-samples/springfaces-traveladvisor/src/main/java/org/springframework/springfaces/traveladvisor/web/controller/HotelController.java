package org.springframework.springfaces.traveladvisor.web.controller;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.Hotel;
import org.springframework.springfaces.traveladvisor.domain.Rating;
import org.springframework.springfaces.traveladvisor.domain.Review;
import org.springframework.springfaces.traveladvisor.domain.ReviewDetails;
import org.springframework.springfaces.traveladvisor.service.CityService;
import org.springframework.springfaces.traveladvisor.service.HotelService;
import org.springframework.springfaces.traveladvisor.service.ReviewsSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HotelController {

	private CityService cityService;
	private HotelService hotelService;

	@RequestMapping("/advisor/{country}/{city}/{name}")
	public String hotel(@PathVariable String country, @PathVariable String city, @PathVariable String name, Model model) {
		Hotel hotel = getHotel(country, city, name);
		model.addAttribute(hotel);
		model.addAttribute("reviewsSummary", toChartModel(hotelService.getReviewSummary(hotel)));
		return "hotel";
	}

	private CartesianChartModel toChartModel(ReviewsSummary reviewSummary) {
		CartesianChartModel model = new CartesianChartModel();
		ChartSeries chartSeries = new ChartSeries();
		for (Rating rating : Rating.values()) {
			chartSeries.set(rating.toString(), reviewSummary.getNumberOfReviewsWithRating(rating));
		}
		model.addSeries(chartSeries);
		return model;
	}

	@RequestMapping("/advisor/{country}/{city}/{name}/review/{index}")
	public String hotelReview(@PathVariable String country, @PathVariable String city, @PathVariable String name,
			@PathVariable int index, Model model) {
		Review review = hotelService.getReview(getHotel(country, city, name), index);
		model.addAttribute(review);
		return "review";
	}

	@RequestMapping("/advisor/{country}/{city}/{name}/write-review")
	public String writeHotelReview(@PathVariable String country, @PathVariable String city, @PathVariable String name,
			Model model) {
		// FIXME is it wise that we can get here from a bookmark. Back button issues?
		ReviewDetails review = new ReviewDetails();
		model.addAttribute(getHotel(country, city, name)).addAttribute("review", review);
		return "writeReview";
	}

	@NavigationMapping
	public NavigationOutcome onSubmitReview(Hotel hotel, ReviewDetails details) {
		/* Review review = */hotelService.addReview(hotel, details);
		// FIXME put something in flash scope to be displayed on hotel screen
		ModelMap model = new ExtendedModelMap();
		model.addAttribute("country", hotel.getCity().getCountry());
		model.addAttribute("city", hotel.getCity().getName());
		model.addAttribute("hotel", hotel.getName());
		return new NavigationOutcome("@hotel", model);
	}

	private Hotel getHotel(String country, String cityName, String hotelName) {
		City city = cityService.getCity(cityName, country);
		Hotel hotel = hotelService.getHotel(city, hotelName);
		return hotel;
	}

	@Autowired
	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}

	@Autowired
	public void setHotelService(HotelService hotelService) {
		this.hotelService = hotelService;
	}
}
