package es.twd.hotel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.twd.hotel.dto.AddressDTO;
import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.HotelUpdateDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.exception.ResourceNotFoundException;
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
	private HotelUpdateDTO hotelUpdateDTO;
	private UpdateAddressDTO updateAddressDTO;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		hotelCreateDTO = HotelCreateDTO.builder().name("Hotel Test").stars(4).address(
				AddressDTO.builder().street("Street 123").city("CityX").country("CountryY").postalCode("12345").build())
				.build();

		hotelResponseDTO = HotelResponseDTO.builder().id(1L).name("Hotel Test").stars(4)
				.address(hotelCreateDTO.getAddress()).build();

		hotelUpdateDTO = HotelUpdateDTO.builder().name("Updated Hotel").stars(5).build();

		updateAddressDTO = UpdateAddressDTO.builder().street("New Street").city("New City").country("New Country")
				.postalCode("54321").build();

	}

	@Test
	void createHotel_shouldReturnCreated() throws Exception {
		when(hotelService.createHotel(hotelCreateDTO)).thenReturn(hotelResponseDTO);

		mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hotelCreateDTO))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(roles = "USER")
	void getAllHotelsWithoutPagination_shouldReturnListOfHotels() throws Exception {
		HotelResponseDTO hotel1 = new HotelResponseDTO(1L, "Hotel Test", 4, hotelCreateDTO.getAddress());

		when(hotelService.getAllHotels()).thenReturn(List.of(hotel1));

		mockMvc.perform(get("/hotels/all")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].name").value("Hotel Test"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void getAllHotelsWithoutPagination_shouldReturnEmptyList() throws Exception {
		when(hotelService.getAllHotels()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/hotels/all")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getAllHotels_shouldReturnPagedHotels() throws Exception {

		HotelResponseDTO dto = new HotelResponseDTO(1L, "Hotel Test", 4, null);

		Pageable pageable = PageRequest.of(0, 10);
		Page<HotelResponseDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

		when(hotelService.getAllHotels(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/hotels?page=0&size=10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].name").value("Hotel Test"))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	void getAllHotels_shouldApplySorting() throws Exception {

		Page<HotelResponseDTO> emptyPage = Page.empty();
		when(hotelService.getAllHotels(any(Pageable.class))).thenReturn(emptyPage);

		mockMvc.perform(get("/hotels?sort=stars,desc")).andExpect(status().isOk());

		verify(hotelService).getAllHotels(argThat(pageable -> pageable.getSort().getOrderFor("stars").isDescending()));
	}

	@Test
	void getAllHotels_shouldUseDefaultPageable() throws Exception {

		Page<HotelResponseDTO> emptyPage = Page.empty();
		when(hotelService.getAllHotels(any(Pageable.class))).thenReturn(emptyPage);

		mockMvc.perform(get("/hotels")).andExpect(status().isOk());

		verify(hotelService).getAllHotels(argThat(pageable -> pageable.getPageNumber() == 0
				&& pageable.getPageSize() == 10 && pageable.getSort().getOrderFor("name").isAscending()));
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

	@Test
	void updateHotel_shouldReturnOk() throws Exception {
		when(hotelService.updateHotel(1L, hotelUpdateDTO)).thenReturn(hotelResponseDTO);

		mockMvc.perform(put("/hotels/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hotelUpdateDTO))).andExpect(status().isOk());
	}

	@Test
	void updateHotelAddress_shouldReturnOk() throws Exception {
		when(hotelService.updateHotelAddress(1L, updateAddressDTO)).thenReturn(hotelResponseDTO);

		mockMvc.perform(put("/hotels/1/address").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateAddressDTO))).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deleteHotel_shouldReturnNoContent() throws Exception {
		doNothing().when(hotelService).deleteHotel(1L);

		mockMvc.perform(delete("/hotels/1")).andExpect(status().isNoContent());
	}

	@Test
	void getHotelById_shouldReturn404_whenNotFound() throws Exception {
		when(hotelService.getHotelById(99L)).thenThrow(new ResourceNotFoundException("Hotel not found"));

		mockMvc.perform(get("/hotels/99")).andExpect(status().isNotFound());
	}

	@Test
	void createHotel_shouldReturn400_whenInvalidJson() throws Exception {
		String invalidJson = "{ \"name\": \"Test Hotel\", \"stars\": \"notANumber\" }";

		mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateHotel_shouldReturn404_whenNotFound() throws Exception {
		when(hotelService.updateHotel(eq(99L), any())).thenThrow(new ResourceNotFoundException("Hotel not found"));

		mockMvc.perform(put("/hotels/99").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hotelCreateDTO))).andExpect(status().isNotFound());
	}

	@Test
	void deleteHotel_shouldReturn404_whenNotFound() throws Exception {
		doThrow(new ResourceNotFoundException("Hotel not found")).when(hotelService).deleteHotel(99L);

		mockMvc.perform(delete("/hotels/99")).andExpect(status().isNotFound());
	}

}
