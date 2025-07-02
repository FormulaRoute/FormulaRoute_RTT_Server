package com.ufscar.formularoute.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ParameterResponse {
    private String key;
    private String value;
    private ZonedDateTime added;

    public ParameterResponse(Parameter entity) {
        this.key = entity.getKey();
        this.value = entity.getValue();
        this.added = entity.getAdded();
    }
}