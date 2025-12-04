package es.twd.hotel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.twd.hotel.dto.AddressDTO;
import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.HotelUpdateDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.entity.Address;
import es.twd.hotel.entity.Hotel;
import es.twd.hotel.exception.ResourceNotFoundException;
import es.twd.hotel.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

	@Mock
	private HotelRepository hotelRepository;

	@InjectMocks
	private HotelService hotelService;

	private Hotel hotel;
	private HotelCreateDTO hotelCreateDTO;
	private UpdateAddressDTO updateAddressDTO;
	private HotelUpdateDTO hotelUpdateDTO;

	@BeforeEach
	void setUp() {
		Address address = new Address();
		address.setStreet("Street 123");
		address.setCity("CityX");
		address.setCountry("CountryY");
		address.setPostalCode("12345");

		hotel = new Hotel();
		hotel.setId(1L);
		hotel.setName("Hotel Test");
		hotel.setStars(4);
		hotel.setAddress(address);

		hotelCreateDTO = HotelCreateDTO.builder().name("Hotel Test").stars(4).address(
				AddressDTO.builder().street("Street 123").city("CityX").country("CountryY").postalCode("12345").build())
				.build();

		updateAddressDTO = UpdateAddressDTO.builder().street("New Street").city("New City").country("New Country")
				.postalCode("54321").build();

		hotelUpdateDTO = HotelUpdateDTO.builder().name("Hotel Updated").stars(5).build();
	}

	@Test
	void createHotel_shouldReturnSavedHotelDTO() {
		when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

		HotelResponseDTO response = hotelService.createHotel(hotelCreateDTO);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("Hotel Test");
		assertThat(response.getStars()).isEqualTo(4);

		verify(hotelRepository, times(1)).save(any(Hotel.class));
	}

	@Test
	void getAllHotels_shouldReturnListOfHotels() {
		when(hotelRepository.findAll()).thenReturn(Collections.singletonList(hotel));

		List<HotelResponseDTO> response = hotelService.getAllHotels();

		assertThat(response).hasSize(1);
		assertThat(response.get(0).getName()).isEqualTo("Hotel Test");
	}

	@Test
	void getHotelById_existingHotel_shouldReturnHotel() {
		when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

		HotelResponseDTO response = hotelService.getHotelById(1L);

		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("Hotel Test");
	}

	@Test
	void getHotelById_nonExistingHotel_shouldThrowException() {
		when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> hotelService.getHotelById(2L)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Hotel not found with id 2");
	}

	@Test
	void getHotelsByCity_shouldReturnHotelsInCity() {
		when(hotelRepository.findByAddress_CityIgnoreCase("CityX")).thenReturn(Collections.singletonList(hotel));

		List<HotelResponseDTO> response = hotelService.getHotelsByCity("CityX");

		assertThat(response).hasSize(1);
		assertThat(response.get(0).getAddress().getCity()).isEqualTo("CityX");
	}

	@Test
	void updateHotelAddress_existingHotel_shouldUpdateAddress() {
		when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
		when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

		HotelResponseDTO response = hotelService.updateHotelAddress(1L, updateAddressDTO);

		assertThat(response.getAddress().getStreet()).isEqualTo("New Street");
		assertThat(response.getAddress().getCity()).isEqualTo("New City");
		assertThat(response.getAddress().getCountry()).isEqualTo("New Country");
		assertThat(response.getAddress().getPostalCode()).isEqualTo("54321");
	}

	@Test
	void updateHotelAddress_nonExistingHotel_shouldThrowException() {
		when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> hotelService.updateHotelAddress(2L, updateAddressDTO))
				.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Hotel not found with id 2");
	}

	@Test
	void updateHotel_existingHotel_shouldUpdateFields() {
		when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
		when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

		HotelResponseDTO response = hotelService.updateHotel(1L, hotelUpdateDTO);

		assertThat(response.getName()).isEqualTo("Hotel Updated");
		assertThat(response.getStars()).isEqualTo(5);
	}

	@Test
	void updateHotel_nonExistingHotel_shouldThrowException() {
		when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> hotelService.updateHotel(2L, hotelUpdateDTO))
				.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Hotel not found with id 2");
	}

	@Test
	void deleteHotel_existingHotel_shouldCallRepositoryDelete() {
		when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

		hotelService.deleteHotel(1L);

		verify(hotelRepository, times(1)).delete(hotel);
	}

	@Test
	void deleteHotel_nonExistingHotel_shouldThrowException() {
		when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class,
				() -> hotelService.deleteHotel(2L));

		verify(hotelRepository, never()).delete(any(Hotel.class));
	}
}
