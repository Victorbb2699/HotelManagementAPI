package es.twd.hotel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.HotelUpdateDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "409", description = "Hotel with same name already exists in the city") })
	@PostMapping
	public ResponseEntity<HotelResponseDTO> createHotel(@RequestBody @Validated HotelCreateDTO dto) {
		return new ResponseEntity<>(hotelService.createHotel(dto), HttpStatus.CREATED);
	}

	@Operation(summary = "Get all hotels (paginated)", description = "Returns a paginated and sortable list of hotels")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Hotels retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access") })
	@GetMapping
	public ResponseEntity<Page<HotelResponseDTO>> getAllHotels(
			@PageableDefault(size = 10, sort = "name") Pageable pageable) {

		Page<HotelResponseDTO> response = hotelService.getAllHotels(pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get hotel by ID", description = "Returns hotel information for a given ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Hotel retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Hotel not found"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access") })
	@GetMapping("/{id}")
	public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long id) {
		return ResponseEntity.ok(hotelService.getHotelById(id));
	}

	@Operation(summary = "Get hotels by city", description = "Returns hotels filtered by city name")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Hotels retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access") })
	@GetMapping("/city/{city}")
	public ResponseEntity<List<HotelResponseDTO>> getHotelsByCity(@PathVariable String city) {
		return ResponseEntity.ok(hotelService.getHotelsByCity(city));
	}

	@Operation(summary = "Update hotel fields", description = "Updates name and stars of the hotel")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
			@ApiResponse(responseCode = "404", description = "Hotel not found"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access") })
	@PutMapping("/{id}")
	public ResponseEntity<HotelResponseDTO> updateHotel(@PathVariable Long id,
			@RequestBody @Validated HotelUpdateDTO dto) {
		return ResponseEntity.ok(hotelService.updateHotel(id, dto));
	}

	@Operation(summary = "Update hotel address", description = "Updates the address of a hotel")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Hotel address updated successfully"),
			@ApiResponse(responseCode = "404", description = "Hotel not found"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access") })
	@PutMapping("/{id}/address")
	public ResponseEntity<HotelResponseDTO> updateHotelAddress(@PathVariable Long id,
			@RequestBody @Validated UpdateAddressDTO dto) {
		return ResponseEntity.ok(hotelService.updateHotelAddress(id, dto));
	}

	@Operation(summary = "Delete a hotel", description = "Deletes a hotel by id. Only admin users allowed")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
			@ApiResponse(responseCode = "403", description = "Forbidden, requires ADMIN role"),
			@ApiResponse(responseCode = "404", description = "Hotel not found"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access") })
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
		hotelService.deleteHotel(id);
		return ResponseEntity.noContent().build();
	}

}
