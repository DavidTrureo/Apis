package com.example.edutech.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminRutChecker {

    private static final Logger logger = LoggerFactory.getLogger(AdminRutChecker.class);
    private final Set<String> adminRuts;

    public AdminRutChecker(@Value("${app.admin.ruts:}") String adminRutsString) {
        if (adminRutsString == null || adminRutsString.isBlank()) {
            this.adminRuts = Collections.emptySet();
            logger.warn("La lista de RUTs de administrador (app.admin.ruts) no está configurada o está vacía. Ningún usuario será considerado administrador por esta vía.");
        } else {
            this.adminRuts = new HashSet<>(Arrays.asList(adminRutsString.split("\\s*,\\s*")));
            logger.info("RUTs de administrador configurados: {}", this.adminRuts);
        }
    }

    public boolean isAdmin(String rut) {
        if (rut == null || rut.isBlank()) {
            logger.debug("Verificación de administrador para RUT nulo o vacío: NO es admin.");
            return false;
        }
        boolean isAdmin = adminRuts.contains(rut);
        if (!isAdmin) {
            logger.debug("Verificación de administrador para RUT '{}': NO es admin. Lista de admins configurados: {}", rut, adminRuts);
        } else {
            logger.debug("Verificación de administrador para RUT '{}': SÍ es admin.", rut);
        }
        return isAdmin;
    }

    public List<String> getAdminRuts() {
        return List.copyOf(adminRuts); // Devuelve una copia inmutable
    }
}