package com.proyecto.invengest.backup.service;

import com.proyecto.invengest.backup.model.RestoreResult;

public interface RestoreService {
    RestoreResult restoreBackup(String filePath);

}
