package com.unitedgo.passenger_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unitedgo.passenger_service.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

	@Query("SELECT p from Passenger p WHERE p.flightId=:flightId AND p.userId=:userId AND p.pnr IS NULL")
	List<Passenger> findByFlightId(String flightId, String userId);
	
	@Query("SELECT COUNT(p) from Passenger p WHERE p.flightId=:flightId AND p.userId=:userId AND p.pnr IS NULL")
	Integer findPassengerCount(String flightId, String userId);
	
	List<Passenger> findByPnr(String pnr);
	
	@Query("UPDATE Passenger p SET p.pnr=:pnr WHERE p.flightId=:flightId AND p.userId=:userId AND p.pnr IS NULL")
	@Modifying
	void updatePassengerPnr(String pnr, String flightId, String userId);
}
