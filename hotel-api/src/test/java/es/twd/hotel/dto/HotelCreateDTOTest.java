package es.twd.hotel.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class HotelCreateDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void whenValidHotelCreateDTO_thenNoViolations() {
		AddressDTO address = AddressDTO.builder().street("Street 123").city("CityX").country("CountryY")
				.postalCode("12345").build();

		HotelCreateDTO dto = HotelCreateDTO.builder().name("Hotel Test").stars(4).address(address).build();

		Set<ConstraintViolation<HotelCreateDTO>> violations = validator.validate(dto);
		assertThat(violations).isEmpty();
	}

	@Test
	void whenInvalidHotelCreateDTO_thenViolations() {
		HotelCreateDTO dto = HotelCreateDTO.builder().name("") // NotBlank violation
				.stars(0) // Min violation
				.address(null) // NotNull violation
				.build();

		Set<ConstraintViolation<HotelCreateDTO>> violations = validator.validate(dto);
		assertThat(violations).hasSize(3);
	}

}
