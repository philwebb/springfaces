package org.springframework.springfaces.showcase.selectitems;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Example JPA Entity.
 * 
 * @author Phillip Webb
 */
@Entity
public class Author {

	@Id
	private Long id;

	private String firstName;

	private String lastName;

	public Author(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
