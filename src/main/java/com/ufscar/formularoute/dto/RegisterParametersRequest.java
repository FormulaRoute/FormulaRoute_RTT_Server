package com.ufscar.formularoute.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RegisterParametersRequest {

    @JsonProperty("lap-name")
    private String lapName;

    private Map<String, String> parameters = new HashMap<>();

    // Esta anotação mágica diz ao Jackson para colocar aqui
    // qualquer propriedade do JSON que não foi mapeada explicitamente.
    @JsonAnySetter
    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }
}