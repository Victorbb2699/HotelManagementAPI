package es.twd.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

	@NotBlank
	private String street;

	@NotBlank
	private String city;

	@NotBlank
	private String country;

	@NotBlank
	@Size(max = 10)
	private String postalCode;
}
