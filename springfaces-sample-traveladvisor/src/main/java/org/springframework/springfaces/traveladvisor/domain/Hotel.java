package org.springframework.springfaces.traveladvisor.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Hotel {

	@Id
	@GeneratedValue
	private Long id;

	private String key;

	private String name;

	@ManyToOne(optional = false)
	private City city;

	protected Hotel() {
	}

	public Hotel(String key, String name) {
		this.key = key;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setCity(City city) {
		this.city = city;
	}
}
