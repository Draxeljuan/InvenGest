package com.proyecto.invengest.backup.controller;


import com.proyecto.invengest.backup.model.BackupResult;
import com.proyecto.invengest.backup.model.RestoreResult;
import com.proyecto.invengest.backup.service.BackupService;
import com.proyecto.invengest.backup.service.RestoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class BackupController {
    private final BackupService backupService;
    private final RestoreService restoreService;

    public BackupController(BackupService backupService, RestoreService restoreService) {
        this.backupService = backupService;
        this.restoreService = restoreService;
    }

    @PostMapping
    public ResponseEntity<BackupResult> createBackup() {
        BackupResult result = backupService.createBackup();
        return ResponseEntity.status(result.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }

    @PostMapping("/restore")
    public ResponseEntity<RestoreResult> restoreBackup(@RequestParam String filePath) {
        RestoreResult result = restoreService.restoreBackup(filePath);
        return ResponseEntity.status(result.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
}
