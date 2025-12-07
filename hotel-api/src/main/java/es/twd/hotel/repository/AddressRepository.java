package es.twd.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.twd.hotel.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	
}
