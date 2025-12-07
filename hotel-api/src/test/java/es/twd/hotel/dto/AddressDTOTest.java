package es.twd.hotel.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AddressDTOTest {
	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void whenValidAddressDTO_thenNoViolations() {
		AddressDTO dto = AddressDTO.builder().street("Street 123").city("CityX").country("CountryY").postalCode("12345")
				.build();
		Set<ConstraintViolation<AddressDTO>> violations = validator.validate(dto);
		assertThat(violations).isEmpty();
	}

	@Test
	void whenInvalidAddressDTO_thenViolations() {
		AddressDTO dto = AddressDTO.builder().street("") // NotBlank
				.city("") // NotBlank
				.country("")// NotBlank
				.postalCode("12345678901") // max 10
				.build();
		Set<ConstraintViolation<AddressDTO>> violations = validator.validate(dto);
		assertThat(violations).hasSize(4);
	}

}
