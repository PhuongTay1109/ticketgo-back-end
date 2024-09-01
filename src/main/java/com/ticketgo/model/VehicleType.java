package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Vehicle_Types")
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

}
