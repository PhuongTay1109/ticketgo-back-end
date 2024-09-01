package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_route_id", nullable = false)
    private BusRoute busRoute;

    @Column(name = "booking_time")
    private Timestamp bookingTime;

    @ManyToMany
    @JoinTable(
            name = "Booking_Seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> seats = new HashSet<>();
}

