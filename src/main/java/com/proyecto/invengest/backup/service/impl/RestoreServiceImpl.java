package com.proyecto.invengest.backup.service.impl;

import com.proyecto.invengest.backup.model.RestoreResult;
import com.proyecto.invengest.backup.service.RestoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RestoreServiceImpl implements RestoreService {
    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public RestoreResult restoreBackup(String filePath) {
        try {
            String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
            ProcessBuilder pb = new ProcessBuilder(
                    "C:/wamp64/bin/mysql/mysql9.1.0/bin/mysqldump.exe",
                    "-u", dbUser,
                    dbName
            );
            pb.redirectInput(new File(filePath));
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return new RestoreResult(true, "Restauración exitosa");
            } else {
                return new RestoreResult(false, "Error en mysql restore");
            }

        } catch (Exception e) {
            return new RestoreResult(false, e.getMessage());
        }
    }
}
