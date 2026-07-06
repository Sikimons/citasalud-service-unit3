package org.ups.citasaludservice.application.port.out;

import org.ups.citasaludservice.domain.model.Cita;

public interface NotificacionGatewayPort {

    void enviarConfirmacion(Cita cita, String numeroWhatsApp);
}
