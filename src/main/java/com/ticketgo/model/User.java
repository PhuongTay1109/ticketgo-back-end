package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "identity_no", nullable = false, unique = true, length = 12)
    private String identityNo;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(nullable = false)
    private String address;

}
