package org.ups.citasaludservice.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.entity.CitaEntity;

import java.util.UUID;

public interface CitaJpaRepository extends JpaRepository<CitaEntity, UUID> {
}
