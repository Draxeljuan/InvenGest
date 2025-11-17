package com.proyecto.invengest.backup.controller;

import com.proyecto.invengest.backup.model.BackupResult;
import com.proyecto.invengest.backup.model.RestoreResult;
import com.proyecto.invengest.backup.service.BackupService;
import com.proyecto.invengest.backup.service.RestoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/backup")

public class BackupController {

    private final BackupService backupService;
    private final RestoreService restoreService;

    @Value("${backup.directory:./backups/}")
    private String backupDir;

    public BackupController(BackupService backupService, RestoreService restoreService) {
        this.backupService = backupService;
        this.restoreService = restoreService;
    }

    /**
     * Crear un nuevo backup de la base de datos
     */
    @PostMapping
    public ResponseEntity<BackupResult> createBackup() {
        try {
            BackupResult result = backupService.createBackup();
            return ResponseEntity.status(result.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(result);
        } catch (Exception e) {
            BackupResult errorResult = new BackupResult(false, null, "Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * Restaurar la base de datos desde un archivo de backup
     */
    @PostMapping("/restore")
    public ResponseEntity<RestoreResult> restoreBackup(@RequestParam String filePath) {
        try {
            RestoreResult result = restoreService.restoreBackup(filePath);
            return ResponseEntity.status(result.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(result);
        } catch (Exception e) {
            RestoreResult errorResult = new RestoreResult(false, "Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * Listar todos los backups disponibles
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listBackups() {
        try {
            File backupDirectory = new File(backupDir);

            if (!backupDirectory.exists() || !backupDirectory.isDirectory()) {
                return ResponseEntity.ok(new ArrayList<>()); // Devolver lista vacía si no existe
            }

            File[] files = backupDirectory.listFiles((dir, name) -> name.endsWith(".sql"));

            if (files == null || files.length == 0) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<Map<String, Object>> backupList = Arrays.stream(files)
                    .map(file -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        try {
                            Path path = Paths.get(file.getAbsolutePath());
                            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

                            fileInfo.put("name", file.getName());
                            fileInfo.put("path", file.getAbsolutePath());
                            fileInfo.put("size", file.length()); // en bytes
                            fileInfo.put("sizeKB", file.length() / 1024); // en KB
                            fileInfo.put("sizeMB", String.format("%.2f", file.length() / (1024.0 * 1024.0))); // en MB
                            fileInfo.put("createdDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(new Date(attrs.creationTime().toMillis())));
                            fileInfo.put("modifiedDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(new Date(file.lastModified())));

                        } catch (Exception e) {
                            fileInfo.put("error", "No se pudo leer información del archivo");
                        }
                        return fileInfo;
                    })
                    .sorted((f1, f2) -> {
                        // Ordenar por fecha de creación (más reciente primero)
                        String date1 = (String) f1.get("createdDate");
                        String date2 = (String) f2.get("createdDate");
                        return date2.compareTo(date1);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(backupList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    /**
     * Eliminar un archivo de backup específico
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteBackup(@RequestParam String fileName) {
        try {
            File backupFile = new File(backupDir + fileName);
            Map<String, Object> response = new HashMap<>();

            if (!backupFile.exists()) {
                response.put("success", false);
                response.put("message", "El archivo no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (backupFile.delete()) {
                response.put("success", true);
                response.put("message", "Backup eliminado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No se pudo eliminar el archivo");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtener información del directorio de backups
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getBackupInfo() {
        try {
            File backupDirectory = new File(backupDir);
            Map<String, Object> info = new HashMap<>();

            info.put("directory", backupDirectory.getAbsolutePath());
            info.put("exists", backupDirectory.exists());

            if (backupDirectory.exists()) {
                File[] files = backupDirectory.listFiles((dir, name) -> name.endsWith(".sql"));
                long totalSize = 0;

                if (files != null) {
                    info.put("totalBackups", files.length);
                    for (File file : files) {
                        totalSize += file.length();
                    }
                } else {
                    info.put("totalBackups", 0);
                }

                info.put("totalSizeBytes", totalSize);
                info.put("totalSizeMB", String.format("%.2f", totalSize / (1024.0 * 1024.0)));
            } else {
                info.put("totalBackups", 0);
                info.put("totalSizeBytes", 0);
                info.put("totalSizeMB", "0.00");
            }

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
