package com.ufscar.formularoute.repository;

import com.ufscar.formularoute.dto.Lap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LapRepository extends JpaRepository<Lap, UUID> {
    Optional<Lap> findByName(String name);
}