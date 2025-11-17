package com.proyecto.invengest.backup.service.impl;

import com.proyecto.invengest.backup.model.BackupResult;
import com.proyecto.invengest.backup.service.BackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BackupServiceImpl implements BackupService {

    @Value("${backup.directory:./backups/}")
    private String backupDir;

    @Value("${backup.mysql.path:}")
    private String mysqlPath;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public BackupResult createBackup() {
        try {
            // Crear directorio de backups si no existe
            File backupDirectory = new File(backupDir);
            if (!backupDirectory.exists()) {
                backupDirectory.mkdirs();
                System.out.println(" Directorio de backups creado: " + backupDirectory.getAbsolutePath());
            }

            // Generar nombre del archivo con timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "backup_" + timestamp + ".sql";
            String filePath = backupDir + fileName;

            // Extraer nombre de la base de datos
            String dbName = extractDatabaseName(dbUrl);

            // Construir comando mysqldump
            String mysqldumpPath = mysqlPath.isEmpty()
                    ? "mysqldump"
                    : Paths.get(mysqlPath, "mysqldump").toString();

            List<String> command = new ArrayList<>();
            command.add(mysqldumpPath);
            command.add("-h");
            command.add("localhost");
            command.add("-P");
            command.add("3306");
            command.add("-u");
            command.add(dbUser);

            // Solo añadir -p si hay contraseña
            if (dbPassword != null && !dbPassword.isEmpty()) {
                command.add("-p" + dbPassword);
            }

            command.add("--add-drop-table");
            command.add("--routines");
            command.add("--triggers");
            command.add(dbName);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(new File(filePath));
            pb.redirectErrorStream(true);

            System.out.println("  Ejecutando backup...");
            System.out.println("  Ruta de salida: " + new File(filePath).getAbsolutePath());
            System.out.println("  Base de datos: " + dbName);

            Process process = pb.start();
            int exitCode = process.waitFor();

            System.out.println(" Código de salida: " + exitCode);

            if (exitCode == 0) {
                File backupFile = new File(filePath);
                long fileSize = backupFile.length();

                if (fileSize > 0) {
                    System.out.println(" Backup creado exitosamente");
                    System.out.println(" Tamaño: " + (fileSize / 1024) + " KB");
                    return new BackupResult(true, filePath, "Backup creado exitosamente (" + (fileSize / 1024) + " KB)");
                } else {
                    System.err.println(" Error: Archivo de backup vacío");
                    return new BackupResult(false, null, "El archivo de backup está vacío");
                }
            } else {
                // Leer errores del proceso
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorMsg = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }

                System.err.println(" Error en mysqldump: " + errorMsg);
                return new BackupResult(false, null, "Error en mysqldump: " + errorMsg.toString());
            }

        } catch (Exception e) {
            System.err.println(" Excepción durante el backup: " + e.getMessage());
            e.printStackTrace();
            return new BackupResult(false, null, "Error: " + e.getMessage());
        }
    }

    private String extractDatabaseName(String url) {
        // jdbc:mysql://localhost:3306/invengest?params → invengest
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        if (dbName.contains("?")) {
            dbName = dbName.substring(0, dbName.indexOf("?"));
        }
        return dbName;
    }
}
