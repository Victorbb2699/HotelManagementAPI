package es.twd.hotel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import es.twd.hotel.dto.AddressDTO;
import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.entity.Address;
import es.twd.hotel.entity.Hotel;

class HotelMapperTest {

	    @Test
	    void testToHotelEntity_andBack() {
	        AddressDTO addressDTO = AddressDTO.builder()
	                .street("Street 123")
	                .city("CityX")
	                .country("CountryY")
	                .postalCode("12345")
	                .build();

	        HotelCreateDTO createDTO = HotelCreateDTO.builder()
	                .name("Hotel Test")
	                .stars(4)
	                .address(addressDTO)
	                .build();

	        Hotel hotel = HotelMapper.toHotelEntity(createDTO);
	        assertThat(hotel).isNotNull();
	        assertThat(hotel.getName()).isEqualTo(createDTO.getName());
	        assertThat(hotel.getStars()).isEqualTo(createDTO.getStars());
	        assertThat(hotel.getAddress().getCity()).isEqualTo(createDTO.getAddress().getCity());

	        HotelResponseDTO responseDTO = HotelMapper.toResponseDTO(hotel);
	        assertThat(responseDTO).isNotNull();
	        assertThat(responseDTO.getName()).isEqualTo(hotel.getName());
	        assertThat(responseDTO.getStars()).isEqualTo(hotel.getStars());
	        assertThat(responseDTO.getAddress().getCity()).isEqualTo(hotel.getAddress().getCity());
	    }

	    @Test
	    void testUpdateAddressEntity() {
	        Address address = new Address();
	        UpdateAddressDTO updateDTO = UpdateAddressDTO.builder()
	                .street("New Street")
	                .city("New City")
	                .country("New Country")
	                .postalCode("54321")
	                .build();

	        HotelMapper.updateAddressEntity(address, updateDTO);
	        assertThat(address.getStreet()).isEqualTo(updateDTO.getStreet());
	        assertThat(address.getCity()).isEqualTo(updateDTO.getCity());
	        assertThat(address.getCountry()).isEqualTo(updateDTO.getCountry());
	        assertThat(address.getPostalCode()).isEqualTo(updateDTO.getPostalCode());
	    }

}
