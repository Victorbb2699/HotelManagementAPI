package es.twd.hotel.mapper;

import es.twd.hotel.dto.AddressDTO;
import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.entity.Address;
import es.twd.hotel.entity.Hotel;

public class HotelMapper {

	public static HotelResponseDTO toResponseDTO(Hotel hotel) {
		if (hotel == null)
			return null;
		return HotelResponseDTO.builder().id(hotel.getId()).name(hotel.getName()).stars(hotel.getStars())
				.address(toAddressDTO(hotel.getAddress())).build();
	}

	public static Hotel toHotelEntity(HotelCreateDTO dto) {
		if (dto == null)
			return null;
		Hotel hotel = new Hotel();
		hotel.setName(dto.getName());
		hotel.setStars(dto.getStars());
		hotel.setAddress(toAddressEntity(dto.getAddress()));
		return hotel;
	}

	public static AddressDTO toAddressDTO(Address address) {
		if (address == null)
			return null;
		return AddressDTO.builder().street(address.getStreet()).city(address.getCity()).country(address.getCountry())
				.postalCode(address.getPostalCode()).build();
	}

	public static Address toAddressEntity(AddressDTO dto) {
		if (dto == null)
			return null;
		Address address = new Address();
		address.setStreet(dto.getStreet());
		address.setCity(dto.getCity());
		address.setCountry(dto.getCountry());
		address.setPostalCode(dto.getPostalCode());
		return address;
	}

	public static void updateAddressEntity(Address address, UpdateAddressDTO dto) {
		if (address == null || dto == null)
			return;
		address.setStreet(dto.getStreet());
		address.setCity(dto.getCity());
		address.setCountry(dto.getCountry());
		address.setPostalCode(dto.getPostalCode());
	}
}
