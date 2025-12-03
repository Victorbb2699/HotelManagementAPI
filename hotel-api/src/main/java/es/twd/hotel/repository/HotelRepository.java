package es.twd.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.twd.hotel.entity.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

	// Buscar hoteles por ciudad (insensible a mayúsculas)
	List<Hotel> findByAddress_CityIgnoreCase(String city);

}
