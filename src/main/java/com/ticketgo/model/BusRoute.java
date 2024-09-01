package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bus_Routes")
public class BusRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ticket_price", nullable = false)
    private Integer ticketPrice;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "travel_duration", nullable = false)
    private LocalTime travelDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private BusStation destinationStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_station_id", nullable = false)
    private BusStation departureStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    public enum Status {
        ACTIVE, INACTIVE
    }
}
