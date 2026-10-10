package com.example.hospital_management.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_slot_doctor_start",
                columnNames = {"doctor_id", "start_time"}
        )
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean booked = false;

    public Slot(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        this.doctor = doctor;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}