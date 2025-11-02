package com.proyecto.invengest.backup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BackupResult {
    private boolean success;
    private String message;

}
