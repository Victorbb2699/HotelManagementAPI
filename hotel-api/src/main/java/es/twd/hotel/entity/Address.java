package es.twd.hotel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Street is mandatory")
	private String street;

	@NotBlank(message = "City is mandatory")
	private String city;

	@NotBlank(message = "Country is mandatory")
	private String country;

	@NotBlank(message = "Postal code is mandatory")
	@Size(max = 10)
	private String postalCode;

}
