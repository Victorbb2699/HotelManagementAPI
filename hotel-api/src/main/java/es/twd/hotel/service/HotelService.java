package es.twd.hotel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.twd.hotel.dto.HotelCreateDTO;
import es.twd.hotel.dto.HotelResponseDTO;
import es.twd.hotel.dto.HotelUpdateDTO;
import es.twd.hotel.dto.UpdateAddressDTO;
import es.twd.hotel.entity.Hotel;
import es.twd.hotel.exception.ResourceNotFoundException;
import es.twd.hotel.mapper.HotelMapper;
import es.twd.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

	private final HotelRepository hotelRepository;

	public HotelResponseDTO createHotel(HotelCreateDTO dto) {
		Hotel hotel = HotelMapper.toHotelEntity(dto);
		Hotel saved = hotelRepository.save(hotel);
		return HotelMapper.toResponseDTO(saved);
	}

	public Page<HotelResponseDTO> getAllHotels(Pageable pageable) {
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
		HotelMapper.updateAddressEntity(hotel.getAddress(), dto);
		Hotel updated = hotelRepository.save(hotel);
		return HotelMapper.toResponseDTO(updated);
	}

	public HotelResponseDTO updateHotel(Long hotelId, HotelUpdateDTO dto) {
		Hotel hotel = hotelRepository.findById(hotelId)
				.orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id " + hotelId));

		if (dto.getName() != null)
			hotel.setName(dto.getName());
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
