package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
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
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_route_id", nullable = false)
    private BusRoute busRoute;

    @Column(name = "booking_time")
    private Timestamp bookingTime;

    @Column(name = "anonymous_name")
    private String anonymousName;

    @Column(name = "anonymous_email")
    private String anonymousEmail;

    @Column(name = "anonymous_address")
    private String anonymousAddress;

    @Column(name = "anonymous_date_of_birth")
    private LocalDate anonymousDateOfBirth;

    @Column(name = "anonymous_phone")
    private String anonymousPhone;

    @Column(name = "anonymous_identity_no")
    private String anonymousIdentityNo;

    @ManyToMany
    @JoinTable(
            name = "Booking_Seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> seats = new HashSet<>();
}

