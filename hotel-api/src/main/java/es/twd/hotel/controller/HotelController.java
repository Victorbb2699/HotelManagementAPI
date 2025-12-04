package es.twd.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Validated
@Tag(name = "Hotels", description = "Hotel management API")
public class HotelController {

	private final HotelService hotelService;

	@Operation(summary = "Create a new hotel", description = "Creates a new hotel with a name, stars and address")
	@PostMapping
	public ResponseEntity<HotelResponseDTO> createHotel(@RequestBody @Validated HotelCreateDTO dto) {
		return new ResponseEntity<>(hotelService.createHotel(dto), HttpStatus.CREATED);
	}

	@Operation(summary = "Get all hotels", description = "Returns a list of all hotels")
	@GetMapping
	public ResponseEntity<List<HotelResponseDTO>> getAllHotels() {
		return ResponseEntity.ok(hotelService.getAllHotels());
	}

	@Operation(summary = "Get hotel by ID", description = "Returns hotel information for a given ID")
	@GetMapping("/{id}")
	public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long id) {
		return ResponseEntity.ok(hotelService.getHotelById(id));
	}

	@Operation(summary = "Get hotels by city", description = "Returns hotels filtered by city name")
	@GetMapping("/city/{city}")
	public ResponseEntity<List<HotelResponseDTO>> getHotelsByCity(@PathVariable String city) {
		return ResponseEntity.ok(hotelService.getHotelsByCity(city));
	}

}
