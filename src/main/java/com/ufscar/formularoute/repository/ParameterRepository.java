package com.ufscar.formularoute.repository;

import com.ufscar.formularoute.dto.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ParameterRepository extends JpaRepository<Parameter, UUID> {

}