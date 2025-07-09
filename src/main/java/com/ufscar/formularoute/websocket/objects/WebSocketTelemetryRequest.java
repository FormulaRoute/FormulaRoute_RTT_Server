package com.ufscar.formularoute.websocket.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class WebSocketTelemetryRequest {
    private String lapName;
    private Map<String, String> parameters;
}