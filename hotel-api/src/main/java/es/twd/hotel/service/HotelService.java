package es.twd.hotel.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.HotelUpdateDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.entity.Hotel;
import es.twd.hotel.exception.ResourceAlreadyExistsException;
import es.twd.hotel.exception.ResourceNotFoundException;
import es.twd.hotel.mapper.HotelMapper;
import es.twd.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

	private final HotelRepository hotelRepository;

	public HotelResponseDTO createHotel(HotelCreateDTO dto) {
		String city = dto.getAddress().getCity();

		boolean exists = hotelRepository.existsByNameAndAddress_CityIgnoreCase(dto.getName(), city);
		if (exists) {
			throw new ResourceAlreadyExistsException(
					"Hotel with name '" + dto.getName() + "' already exists in city '" + city + "'");
		}
		Hotel hotel = HotelMapper.toHotelEntity(dto);
		Hotel saved = hotelRepository.save(hotel);
		return HotelMapper.toResponseDTO(saved);
	}

	public Page<HotelResponseDTO> getAllHotels(Pageable pageable) {

		pageable.getSort().forEach(order -> {
			String prop = order.getProperty();
			if (!Set.of("name", "stars", "address.city", "address.country").contains(prop)) {
				throw new IllegalArgumentException("Invalid sort property: " + prop);
			}
		});
		Page<Hotel> hotels = hotelRepository.findAll(pageable);
		return hotels.map(HotelMapper::toResponseDTO);
	}

	public HotelResponseDTO getHotelById(Long hotelId) {
		Hotel hotel = hotelRepository.findById(hotelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));
		return HotelMapper.toResponseDTO(hotel);
	}

	public List<HotelResponseDTO> getHotelsByCity(String city) {
		return hotelRepository.findByAddress_CityIgnoreCase(city).stream().map(HotelMapper::toResponseDTO)
				.collect(Collectors.toList());
	}

	public HotelResponseDTO updateHotelAddress(Long hotelId, UpdateAddressDTO dto) {
		Hotel hotel = hotelRepository.findById(hotelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));

		String newCity = dto.getCity();
		String hotelName = hotel.getName();

		if (!hotel.getAddress().getCity().equalsIgnoreCase(newCity)) {
			if (hotelRepository.existsByNameAndAddress_CityIgnoreCase(hotelName, newCity)) {
				throw new ResourceAlreadyExistsException(
						"Hotel with name '" + hotelName + "' already exists in city '" + newCity + "'");
			}
		}

		HotelMapper.updateAddressEntity(hotel.getAddress(), dto);
		Hotel updated = hotelRepository.save(hotel);
		return HotelMapper.toResponseDTO(updated);
	}

	public HotelResponseDTO updateHotel(Long hotelId, HotelUpdateDTO dto) {
		Hotel hotel = hotelRepository.findById(hotelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));
		if (dto.getName() != null) {
			String newName = dto.getName();
			String city = hotel.getAddress().getCity();
			if (hotelRepository.existsByNameAndAddress_CityIgnoreCase(newName, city)) {
				throw new ResourceAlreadyExistsException(
						"Hotel with name '" + newName + "' already exists in city '" + city + "'");
			}
			hotel.setName(newName);
		}

		if (dto.getStars() != null)
			hotel.setStars(dto.getStars());

		Hotel updated = hotelRepository.save(hotel);
		return HotelMapper.toResponseDTO(updated);
	}

	public void deleteHotel(Long hotelId) {
		Hotel hotel = hotelRepository.findById(hotelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));
		hotelRepository.delete(hotel);
	}

}
