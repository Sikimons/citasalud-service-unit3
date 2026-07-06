package org.ups.citasaludservice.adapter.out.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.ups.citasaludservice.application.port.out.NotificacionGatewayPort;
import org.ups.citasaludservice.domain.model.Cita;

public class WhatsAppNotificacionAdapter implements NotificacionGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificacionAdapter.class);

    @Override
    @Async
    public void enviarConfirmacion(Cita cita, String numeroWhatsApp) {
        log.info("Enviando confirmación WhatsApp a {} para cita {}", numeroWhatsApp, cita.id());
        // Integration with WhatsApp Business API goes here
    }
}
