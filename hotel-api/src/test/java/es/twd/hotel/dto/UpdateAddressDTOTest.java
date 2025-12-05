package es.twd.hotel.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UpdateAddressDTOTest {
	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void whenValidUpdateAddressDTO_thenNoViolations() {
		UpdateAddressDTO dto = UpdateAddressDTO.builder().street("Street 123").city("CityX").country("CountryY")
				.postalCode("12345").build();
		Set<ConstraintViolation<UpdateAddressDTO>> violations = validator.validate(dto);
		assertThat(violations).isEmpty();
	}

	@Test
	void whenInvalidUpdateAddressDTO_thenViolations() {
		UpdateAddressDTO dto = UpdateAddressDTO.builder().street("") // NotBlank
				.city("") // NotBlank
				.country("")// NotBlank
				.postalCode("12345678901") // max 10
				.build();
		Set<ConstraintViolation<UpdateAddressDTO>> violations = validator.validate(dto);
		assertThat(violations).hasSize(4);
	}

}
