package com.rocketseat.planner.repository;

import com.rocketseat.planner.trip.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
     
}
