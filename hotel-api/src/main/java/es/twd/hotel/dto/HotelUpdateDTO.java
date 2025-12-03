package es.twd.hotel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelUpdateDTO {

	private String name; // puede ser opcional, se permite null para que no se actualice el dato

	@Min(value = 1, message = "Stars must be at least 1")
	@Max(value = 5, message = "Stars cannot exceed 5")
	private Integer stars; // puede ser opcional, se permite null para que no se actualice el dato
}
