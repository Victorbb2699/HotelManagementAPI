package es.twd.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelCreateDTO {

	@Schema(example = "Hotel Example")
	@NotBlank(message = "Hotel name is required")
	@Pattern(regexp = ".*[a-zA-Z].*", message = "Hotel name must contain at least one letter")
	private String name;

	@Min(value = 1, message = "Stars must be at least 1")
	@Max(value = 5, message = "Stars cannot exceed 5")
	private int stars;

	@NotNull(message = "Address is required")
	private AddressDTO address;
}
