package es.twd.hotel.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.twd.hotel.dto.AddressDTO;
import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.service.HotelService;

@WebMvcTest(HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
class HotelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HotelService hotelService;

	private ObjectMapper objectMapper;
	private HotelCreateDTO hotelCreateDTO;
	private HotelResponseDTO hotelResponseDTO;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		hotelCreateDTO = HotelCreateDTO.builder().name("Hotel Test").stars(4).address(
				AddressDTO.builder().street("Street 123").city("CityX").country("CountryY").postalCode("12345").build())
				.build();

		hotelResponseDTO = HotelResponseDTO.builder().id(1L).name("Hotel Test").stars(4)
				.address(hotelCreateDTO.getAddress()).build();
	}

	@Test
	void createHotel_shouldReturnCreated() throws Exception {
		when(hotelService.createHotel(hotelCreateDTO)).thenReturn(hotelResponseDTO);

		mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hotelCreateDTO))).andExpect(status().isCreated());
	}

	@Test
	void getAllHotels_shouldReturnOk() throws Exception {
		when(hotelService.getAllHotels()).thenReturn(Collections.singletonList(hotelResponseDTO));

		mockMvc.perform(get("/hotels")).andExpect(status().isOk());
	}

	@Test
	void getHotelById_shouldReturnOk() throws Exception {
		when(hotelService.getHotelById(1L)).thenReturn(hotelResponseDTO);

		mockMvc.perform(get("/hotels/1")).andExpect(status().isOk());
	}

	@Test
	void getHotelsByCity_shouldReturnOk() throws Exception {
		when(hotelService.getHotelsByCity("CityX")).thenReturn(Collections.singletonList(hotelResponseDTO));

		mockMvc.perform(get("/hotels/city/CityX")).andExpect(status().isOk());
	}
}
