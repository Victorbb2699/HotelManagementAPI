package es.twd.hotel.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class HotelUpdateDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void whenValidHotelUpdateDTO_thenNoViolations() {
		HotelUpdateDTO dto = HotelUpdateDTO.builder().name("Updated Name").stars(5).build();
		Set<ConstraintViolation<HotelUpdateDTO>> violations = validator.validate(dto);
		assertThat(violations).isEmpty();
	}

	@Test
	void whenInvalidStars_thenViolations() {
		HotelUpdateDTO dto = HotelUpdateDTO.builder().stars(0).build(); // min violation
		Set<ConstraintViolation<HotelUpdateDTO>> violations = validator.validate(dto);
		assertThat(violations).hasSize(1);
	}

	@Test
	void whenUpdateNameOnlyNumbers_thenViolation() {
		HotelUpdateDTO dto = HotelUpdateDTO.builder().name("9876").build();

		Set<ConstraintViolation<HotelUpdateDTO>> violations = validator.validate(dto);
		assertThat(violations).extracting("message").contains("Hotel name must contain at least one letter");
	}

}
