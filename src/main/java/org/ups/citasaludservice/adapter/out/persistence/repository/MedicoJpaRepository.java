package org.ups.citasaludservice.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.entity.MedicoEntity;

import java.util.List;
import java.util.UUID;

public interface MedicoJpaRepository extends JpaRepository<MedicoEntity, UUID> {

    List<MedicoEntity> findByEspecialidad(String especialidad);
}
