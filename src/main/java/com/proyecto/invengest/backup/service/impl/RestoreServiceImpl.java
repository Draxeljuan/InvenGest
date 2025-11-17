package com.proyecto.invengest.backup.service.impl;

import com.proyecto.invengest.backup.model.RestoreResult;
import com.proyecto.invengest.backup.service.RestoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestoreServiceImpl implements RestoreService {

    @Value("${backup.mysql.path:}")
    private String mysqlPath;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public RestoreResult restoreBackup(String filePath) {
        try {
            File backupFile = new File(filePath);

            if (!backupFile.exists()) {
                System.err.println(" Error: Archivo de backup no existe: " + filePath);
                return new RestoreResult(false, "El archivo de backup no existe");
            }

            // Extraer nombre de la base de datos
            String dbName = extractDatabaseName(dbUrl);

            // Construir comando mysql (NO mysqldump)
            String mysqlCommand = mysqlPath.isEmpty()
                    ? "mysql"
                    : Paths.get(mysqlPath, "mysql").toString();

            List<String> command = new ArrayList<>();
            command.add(mysqlCommand);
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

            command.add(dbName);

            ProcessBuilder pb = new ProcessBuilder(command);

            System.out.println(" Ejecutando restauración...");
            System.out.println(" Archivo: " + backupFile.getAbsolutePath());
            System.out.println(" Base de datos: " + dbName);

            pb.redirectInput(backupFile);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Leer salida combinada (stdout + stderr)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println(" Código de salida: " + exitCode);

            if (exitCode == 0) {
                System.out.println(" Restauración exitosa");
                return new RestoreResult(true, "Base de datos restaurada exitosamente");
            } else {
                System.err.println(" Error en mysql restore: " + output);
                return new RestoreResult(false, "Error en mysql restore: " + output.toString());
            }

        } catch (Exception e) {
            System.err.println(" Excepción durante la restauración: " + e.getMessage());
            e.printStackTrace();
            return new RestoreResult(false, "Error: " + e.getMessage());
        }
    }

    private String extractDatabaseName(String url) {
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        if (dbName.contains("?")) {
            dbName = dbName.substring(0, dbName.indexOf("?"));
        }
        return dbName;
    }
}
