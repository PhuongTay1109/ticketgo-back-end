package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Bus_Stations")
public class BusStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String address;

    @Column(name = "station_name", nullable = false, length = 100)
    private String stationName;

    @Column(nullable = false, length = 100)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        ACTIVE, INACTIVE
    }
}
