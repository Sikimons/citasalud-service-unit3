package org.ups.citasaludservice.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.entity.PacienteEntity;

import java.util.UUID;

public interface PacienteJpaRepository extends JpaRepository<PacienteEntity, UUID> {
}
