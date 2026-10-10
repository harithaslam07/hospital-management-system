package com.example.hospital_management.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 5)
    private String bloodGroup;

    @Column(length = 255)
    private String address;

    public Patient(User user, LocalDate dateOfBirth, String phone) {
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
    }
}
