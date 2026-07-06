package org.ups.citasaludservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ups.citasaludservice.adapter.out.notification.WhatsAppNotificacionAdapter;
import org.ups.citasaludservice.adapter.out.persistence.CitaJpaAdapter;
import org.ups.citasaludservice.adapter.out.persistence.FranjaHorariaJpaAdapter;
import org.ups.citasaludservice.adapter.out.persistence.MedicoJpaAdapter;
import org.ups.citasaludservice.adapter.out.persistence.PacienteJpaAdapter;
import org.ups.citasaludservice.adapter.out.persistence.mapper.CitaPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.mapper.FranjaHorariaPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.mapper.MedicoPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.mapper.PacientePersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.repository.CitaJpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.repository.FranjaHorariaJpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.repository.MedicoJpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.repository.PacienteJpaRepository;
import org.ups.citasaludservice.adapter.in.web.mapper.CitaWebMapper;
import org.ups.citasaludservice.adapter.in.web.mapper.FranjaHorariaWebMapper;
import org.ups.citasaludservice.adapter.in.web.mapper.MedicoWebMapper;
import org.ups.citasaludservice.application.port.out.*;
import org.ups.citasaludservice.application.service.ConsultarDisponibilidadInteractor;
import org.ups.citasaludservice.application.service.ReservarCitaInteractor;

@Configuration
public class BeanConfiguration {

    @Bean
    CitaPersistenceMapper citaPersistenceMapper() {
        return new CitaPersistenceMapper();
    }

    @Bean
    FranjaHorariaPersistenceMapper franjaHorariaPersistenceMapper() {
        return new FranjaHorariaPersistenceMapper();
    }

    @Bean
    MedicoPersistenceMapper medicoPersistenceMapper() {
        return new MedicoPersistenceMapper();
    }

    @Bean
    PacientePersistenceMapper pacientePersistenceMapper() {
        return new PacientePersistenceMapper();
    }

    @Bean
    CitaRepositoryPort citaRepositoryPort(CitaJpaRepository repo, CitaPersistenceMapper mapper) {
        return new CitaJpaAdapter(repo, mapper);
    }

    @Bean
    FranjaHorariaRepositoryPort franjaHorariaRepositoryPort(FranjaHorariaJpaRepository repo,
                                                             FranjaHorariaPersistenceMapper mapper) {
        return new FranjaHorariaJpaAdapter(repo, mapper);
    }

    @Bean
    MedicoRepositoryPort medicoRepositoryPort(MedicoJpaRepository repo, MedicoPersistenceMapper mapper) {
        return new MedicoJpaAdapter(repo, mapper);
    }

    @Bean
    PacienteRepositoryPort pacienteRepositoryPort(PacienteJpaRepository repo, PacientePersistenceMapper mapper) {
        return new PacienteJpaAdapter(repo, mapper);
    }

    @Bean
    NotificacionGatewayPort notificacionGatewayPort() {
        return new WhatsAppNotificacionAdapter();
    }

    @Bean
    ReservarCitaInteractor reservarCitaUseCase(FranjaHorariaRepositoryPort franjaRepo,
                                               CitaRepositoryPort citaRepo,
                                               PacienteRepositoryPort pacienteRepo,
                                               NotificacionGatewayPort notificacionGateway) {
        return new ReservarCitaInteractor(franjaRepo, citaRepo, pacienteRepo, notificacionGateway);
    }

    @Bean
    ConsultarDisponibilidadInteractor consultarDisponibilidadUseCase(MedicoRepositoryPort medicoRepo,
                                                                      FranjaHorariaRepositoryPort franjaRepo) {
        return new ConsultarDisponibilidadInteractor(medicoRepo, franjaRepo);
    }

    @Bean
    MedicoWebMapper medicoWebMapper() {
        return new MedicoWebMapper();
    }

    @Bean
    FranjaHorariaWebMapper franjaHorariaWebMapper() {
        return new FranjaHorariaWebMapper();
    }

    @Bean
    CitaWebMapper citaWebMapper(MedicoWebMapper medicoWebMapper, FranjaHorariaWebMapper franjaHorariaWebMapper) {
        return new CitaWebMapper(medicoWebMapper, franjaHorariaWebMapper);
    }
}
