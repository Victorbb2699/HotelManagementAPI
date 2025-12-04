package es.twd.hotel.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;
	private HttpServletRequest request;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("/hotels/1");
	}

	@Test
	void handleValidationErrors_shouldReturn400() {

		// Mock FieldError
		FieldError fieldError = new FieldError("hotelCreateDTO", "name", "must not be blank");

		// Mock BindingResult
		var bindingResult = Mockito.mock(org.springframework.validation.BindingResult.class);
		Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

		var response = handler.handleValidationErrors(ex, request);

		assertEquals(400, response.getStatusCode().value());
		assertEquals("name: must not be blank", response.getBody().getMessage());
	}

	@Test
	void handleConstraintViolations_shouldReturn400() {

		ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
		Path path = Mockito.mock(Path.class);

		Mockito.when(path.toString()).thenReturn("hotel.city");
		Mockito.when(violation.getPropertyPath()).thenReturn(path);
		Mockito.when(violation.getMessage()).thenReturn("size must be >= 3");

		ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

		var response = handler.handleConstraintViolations(ex, request);

		assertEquals(400, response.getStatusCode().value());
		assertEquals("hotel.city: size must be >= 3", response.getBody().getMessage());
	}

	@Test
	void handleMalformedJson_shouldReturn400() {

		HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

		var response = handler.handleMalformedJson(ex, request);

		assertEquals(400, response.getStatusCode().value());
		assertEquals("Malformed JSON request", response.getBody().getMessage());
	}

	@Test
	void handleResourceNotFound_shouldReturn404() {
		ResourceNotFoundException ex = new ResourceNotFoundException("Hotel not found");

		ResponseEntity<ApiError> response = handler.handleResourceNotFound(ex, request);

		assertEquals(404, response.getStatusCode().value());
		assertEquals("Hotel not found", response.getBody().getMessage());
		assertEquals("/hotels/1", response.getBody().getPath());
	}

	@Test
	void handleDataIntegrity_shouldReturn409() {

		DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate value");

		var response = handler.handleDataIntegrity(ex, request);

		assertEquals(409, response.getStatusCode().value());
		assertEquals("Data integrity violation", response.getBody().getMessage());
	}

	@Test
	void handleGeneralException_shouldReturn500() {
		Exception ex = new Exception("Something failed");

		ResponseEntity<ApiError> response = handler.handleGeneral(ex, request);

		assertEquals(500, response.getStatusCode().value());
		assertEquals("Unexpected error occurred", response.getBody().getMessage());
	}

}
