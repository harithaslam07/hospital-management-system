package com.example.hospital_management.repository;

import com.example.hospital_management.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("select d from Doctor d join fetch d.user")
    List<Doctor> findAllWithUser();
}