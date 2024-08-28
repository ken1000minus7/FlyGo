package com.unitedgo.booking_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unitedgo.booking_service.entity.Booking;


@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

	@Query("SELECT SUM(b.passengerCount) FROM Booking b WHERE b.flightId=:flightId AND b.status <> 'CANCELLED'")
	Optional<Integer> getPassengerCount(String flightId);
	
	Optional<Booking> findByPnrAndUserId(String pnr, String userId);
	
	@Query("UPDATE Booking b SET b.status=:status WHERE b.pnr=:pnr AND b.userId=:userId")
	@Modifying
	void updateBookingStatus(Booking.Status status, String pnr, String userId);
	
	boolean existsByPnrAndUserId(String pnr, String userId);
	
}
