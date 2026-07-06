package org.ups.citasaludservice.adapter.out.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.ups.citasaludservice.adapter.out.persistence.entity.FranjaHorariaEntity;
import org.ups.citasaludservice.domain.model.EstadoFranja;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FranjaHorariaJpaRepository extends JpaRepository<FranjaHorariaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FranjaHorariaEntity f WHERE f.id = :id")
    Optional<FranjaHorariaEntity> findByIdWithLock(@Param("id") UUID id);

    List<FranjaHorariaEntity> findByMedicoIdAndFecha(UUID medicoId, LocalDate fecha);

    List<FranjaHorariaEntity> findByMedicoIdAndFechaAndEstado(UUID medicoId, LocalDate fecha, EstadoFranja estado);
}
