package es.safareyes.educago.scheduler;

import es.safareyes.educago.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionScheduler {

    private final INotificacionService notificacionService;

    // Se ejecuta automáticamente cada 15 minutos
    @Scheduled(cron = "0 */15 * * * *")
    public void ejecutarRecordatorio24h() {
        log.info("Iniciando verificación automática de recordatorios de 24 horas...");
        notificacionService.enviarRecordatorios24Horas();
    }
}