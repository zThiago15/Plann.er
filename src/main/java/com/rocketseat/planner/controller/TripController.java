package com.rocketseat.planner.controller;

import com.rocketseat.planner.repository.TripRepository;
import com.rocketseat.planner.services.ParticipantService;
import com.rocketseat.planner.trip.entities.Trip;
import com.rocketseat.planner.trip.entities.TripRequestDTO;
import com.rocketseat.planner.trip.entities.TripResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepository tripRepository;

    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(@RequestBody TripRequestDTO trip) {
        Trip newTrip = new Trip(trip);

        this.tripRepository.save(newTrip);

        this.participantService.registerParticipantsToEvent(trip.emails_to_invite(), newTrip.getId());

        return ResponseEntity.ok(new TripResponseDTO(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetailsById(@PathVariable("id") UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        // Return trip if exists or else return not found
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable("id") UUID id, @RequestBody TripRequestDTO tripPayload) {
        Optional<Trip> tripOptional = this.tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip tripToUpdate = tripOptional.get();
            tripToUpdate.setStartsAt(LocalDateTime.parse(tripPayload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            tripToUpdate.setEndsAt(LocalDateTime.parse(tripPayload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            tripToUpdate.setDestination(tripPayload.destination());

            this.tripRepository.save(tripToUpdate);

            return ResponseEntity.ok(tripToUpdate);
        }

        return ResponseEntity.notFound().build();
    }
}
