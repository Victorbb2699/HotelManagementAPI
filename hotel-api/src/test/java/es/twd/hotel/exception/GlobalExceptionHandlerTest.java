package es.twd.hotel.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

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
	void handleResourceNotFound_shouldReturn404() {
		ResourceNotFoundException ex = new ResourceNotFoundException("Hotel not found");

		ResponseEntity<ApiError> response = handler.handleResourceNotFound(ex, request);

		assertEquals(404, response.getStatusCode().value());
		assertEquals("Hotel not found", response.getBody().getMessage());
		assertEquals("/hotels/1", response.getBody().getPath());
	}

	@Test
	void handleGeneralException_shouldReturn500() {
		Exception ex = new Exception("Something failed");

		ResponseEntity<ApiError> response = handler.handleGeneral(ex, request);

		assertEquals(500, response.getStatusCode().value());
		assertEquals("Unexpected error occurred", response.getBody().getMessage());
	}

}
