package com.proyecto.invengest.backup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestoreResult {
    private boolean success;
    private String message;
}
