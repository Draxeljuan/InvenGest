package com.proyecto.invengest.backup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BackupResult {
    private boolean success;
    private String filepath;
    private String message;

    // Constructor alternativo para mantener compatibilidad
    public BackupResult(boolean success, String filepath) {
        this.success = success;
        this.filepath = filepath;
        this.message = null;
    }
}
