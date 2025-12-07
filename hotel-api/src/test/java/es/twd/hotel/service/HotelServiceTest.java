package es.twd.hotel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.twd.hotel.dto.AddressDTO;
import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.HotelUpdateDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.entity.Address;
import es.twd.hotel.entity.Hotel;
import es.twd.hotel.exception.ResourceAlreadyExistsException;
import es.twd.hotel.exception.ResourceNotFoundException;
import es.twd.hotel.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

	@Mock
	private HotelRepository hotelRepository;

	@InjectMocks
	private HotelService hotelService;

	private Hotel hotel;
	private Hotel otherHotel;
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

		Address address2 = new Address();
		address2.setStreet("Street 456");
		address2.setCity("CityX");
		address2.setCountry("CountryY");
		address2.setPostalCode("54321");

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

		otherHotel = Hotel.builder().id(2L).name("Hotel Test Duplicate").stars(3).address(address).build();
		otherHotel = Hotel.builder().id(2L).name("Hotel Test Duplicate").stars(3).address(address2).build();
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
	void createHotel_existingHotel_throwsException() {
		when(hotelRepository.existsByNameAndAddress_CityIgnoreCase("Hotel Test", "CityX")).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> hotelService.createHotel(hotelCreateDTO));
		verify(hotelRepository, never()).save(any());
	}

	@Test
	void getAllHotels_shouldReturnPagedHotels() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Hotel> page = new PageImpl<>(List.of(hotel), pageable, 1);

		when(hotelRepository.findAll(pageable)).thenReturn(page);

		Page<HotelResponseDTO> response = hotelService.getAllHotels(pageable);

		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getName()).isEqualTo("Hotel Test");
		assertThat(response.getTotalElements()).isEqualTo(1);

		verify(hotelRepository).findAll(pageable);
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
	void updateHotel_changeName_noConflict_success() {
		when(hotelRepository.findById(hotel.getId())).thenReturn(Optional.of(hotel));
		when(hotelRepository.existsByNameAndAddress_CityIgnoreCase(anyString(), anyString())).thenReturn(false);
		when(hotelRepository.save(hotel)).thenReturn(hotel);

		HotelResponseDTO response = hotelService.updateHotel(hotel.getId(), hotelUpdateDTO);

		assertThat(response.getName()).isEqualTo("Hotel Updated");
		assertThat(response.getStars()).isEqualTo(5);
		verify(hotelRepository).save(hotel);
	}

	@Test
	void updateHotel_changeName_conflict_throwsException() {
		when(hotelRepository.findById(hotel.getId())).thenReturn(Optional.of(hotel));
		when(hotelRepository.existsByNameAndAddress_CityIgnoreCase(anyString(), anyString())).thenReturn(true);

		hotelUpdateDTO.setName(otherHotel.getName());

		assertThrows(ResourceAlreadyExistsException.class,
				() -> hotelService.updateHotel(hotel.getId(), hotelUpdateDTO));

		verify(hotelRepository, never()).save(any());
	}

	@Test
	void updateHotel_nonExistingHotel_throwsNotFound() {
		when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> hotelService.updateHotel(99L, hotelUpdateDTO));
	}

	@Test
	void updateHotelAddress_changeCity_conflict_throwsException_usingOtherHotel() {
		UpdateAddressDTO conflictingDTO = UpdateAddressDTO.builder().street("New Street").city("CityConflict")
				.country("CountryY").postalCode("54321").build();

		when(hotelRepository.findById(hotel.getId())).thenReturn(Optional.of(hotel));

		when(hotelRepository.existsByNameAndAddress_CityIgnoreCase(hotel.getName(), conflictingDTO.getCity()))
				.thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class,
				() -> hotelService.updateHotelAddress(hotel.getId(), conflictingDTO));

		verify(hotelRepository, never()).save(any(Hotel.class));
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

	@Test
	void whenCreateDuplicateHotel_thenThrowException() {
		AddressDTO address = AddressDTO.builder().street("Street 123").city("CityX").country("CountryY")
				.postalCode("12345").build();
		HotelCreateDTO dto = HotelCreateDTO.builder().name("Hotel Test").stars(4).address(address).build();

		when(hotelRepository.existsByNameAndAddress_CityIgnoreCase(dto.getName(), dto.getAddress().getCity()))
				.thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> hotelService.createHotel(dto));
	}

	@Test
	void getAllHotels_sortedByCityAndCountry_shouldReturnCorrectOrder() {
		Pageable pageable = PageRequest.of(0, 10,
				Sort.by("address.city").ascending().and(Sort.by("address.country").descending()));

		Address address1 = Address.builder().street("Street 1").city("Madrid").country("Spain").postalCode("28001")
				.build();
		Address address2 = Address.builder().street("Street 2").city("Barcelona").country("Spain").postalCode("08001")
				.build();
		Address address3 = Address.builder().street("Street 3").city("Madrid").country("France").postalCode("75001")
				.build();

		Hotel hotel1 = Hotel.builder().id(1L).name("Hotel A").stars(4).address(address1).build();
		Hotel hotel2 = Hotel.builder().id(2L).name("Hotel B").stars(3).address(address2).build();
		Hotel hotel3 = Hotel.builder().id(3L).name("Hotel C").stars(5).address(address3).build();

		when(hotelRepository.findAll(pageable))
				.thenReturn(new PageImpl<>(List.of(hotel2, hotel3, hotel1), pageable, 3));

		Page<HotelResponseDTO> result = hotelService.getAllHotels(pageable);

		assertThat(result.getContent().get(0).getAddress().getCity()).isEqualTo("Barcelona");
		assertThat(result.getContent().get(1).getAddress().getCountry()).isEqualTo("France");
		assertThat(result.getContent().get(2).getAddress().getCity()).isEqualTo("Madrid");
	}

}
