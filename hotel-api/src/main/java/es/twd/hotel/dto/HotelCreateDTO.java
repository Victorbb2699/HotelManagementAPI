package es.twd.hotel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelCreateDTO {

	@NotBlank(message = "Hotel name is required")
	private String name;

	@Min(value = 1, message = "Stars must be at least 1")
	@Max(value = 5, message = "Stars cannot exceed 5")
	private int stars;

	@NotNull(message = "Address is required")
	private AddressDTO address;
}
