package com.ufscar.formularoute.repository;

import com.ufscar.formularoute.dto.Lap;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LapRepository extends JpaRepository<Lap, UUID> {
    @EntityGraph(value = "Lap.withParameters")
    Optional<Lap> findByName(String name);
}