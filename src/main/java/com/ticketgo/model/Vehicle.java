package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_company_id", nullable = false)
    private BusCompany busCompany;

}
