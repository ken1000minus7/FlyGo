package com.unitedgo.passenger_service.model;

import java.util.List;

import com.unitedgo.passenger_service.entity.Passenger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassengersResponse {
	private List<Passenger> passengers;
}
