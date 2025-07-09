package com.ufscar.formularoute.websocket;

import com.ufscar.formularoute.dto.Lap;
import com.ufscar.formularoute.dto.Parameter;
import com.ufscar.formularoute.repository.LapRepository;
import com.ufscar.formularoute.websocket.objects.WebSocketTelemetryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class TelemetryWebSocketController {

    @Autowired
    private LapRepository lapRepository;

    /**
     * Endpoint WebSocket para receber e registrar parâmetros de telemetria.
     * Escuta no destino "/app/sendTelemetry".
     *
     * @param request O objeto contendo o nome da volta e um mapa de parâmetros.
     */
    @MessageMapping("/register")
    @Transactional
    public void handleGenericTelemetry(WebSocketTelemetryRequest request) {
        if (request.getLapName() == null || request.getParameters() == null) {
            System.err.println("Requisição WebSocket inválida: lapName ou parâmetros nulos.");
            return;
        }

        Lap lap = lapRepository.findByName(request.getLapName())
                .orElseGet(() -> {
                    System.out.println("Criando nova Lap via WebSocket: " + request.getLapName());
                    Lap newLap = new Lap();
                    newLap.setName(request.getLapName());
                    return newLap;
                });

        List<Parameter> newParameters = request.getParameters().entrySet().stream()
                .map(entry -> {
                    System.out.println("  - Recebido: " + entry.getKey() + " = " + entry.getValue());
                    Parameter parameter = new Parameter();
                    parameter.setKey(entry.getKey());
                    parameter.setValue(entry.getValue());
                    parameter.setAdded(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
                    parameter.setLap(lap);
                    return parameter;
                })
                .toList();

        lap.getParameters().addAll(newParameters);
        lapRepository.save(lap);

        System.out.println("Parâmetros registrados com sucesso para a Lap: " + lap.getName());
    }
}