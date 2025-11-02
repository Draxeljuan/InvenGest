package com.proyecto.invengest.backup.service.impl;

import com.proyecto.invengest.backup.model.BackupResult;
import com.proyecto.invengest.backup.service.BackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupServiceImpl implements BackupService {

    @Value("${backup.directory:/opt/invenGest/backups/}")
    private String backupDir;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public BackupResult createBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_" + timestamp + ".sql";
        String filePath = backupDir + fileName;

        try {
            String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
            ProcessBuilder pb = new ProcessBuilder(
                    "C:/wamp64/bin/mysql/mysql9.1.0/bin/mysqldump.exe",
                    "-u", dbUser,
                    dbName
            );
            System.out.println("Ejecutando mysqldump...");
            System.out.println("Ruta de salida: " + filePath);
            System.out.println("Base de datos: " + dbName);
            pb.redirectOutput(new File(filePath));
            Process process = pb.start();
            int exitCode = process.waitFor();
            System.out.println("Código de salida: " + exitCode);
            if (exitCode == 0) {
                return new BackupResult(true, filePath);
            } else {
                return new BackupResult(false, "Error en mysqldump");
            }


        } catch (Exception e) {
            return new BackupResult(false, e.getMessage());
        }
    }
}
