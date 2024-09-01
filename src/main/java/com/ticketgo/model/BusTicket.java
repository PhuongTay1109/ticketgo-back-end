package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bus_Tickets")
public class BusTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 50)
    private String ticketNumber;

    @Column(name = "issued_at")
    private Timestamp issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        ACTIVE, CANCELLED, USED
    }
}
