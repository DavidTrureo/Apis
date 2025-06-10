//REALIZADO POR: David Trureo

package com.example.edutech.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.edutech.curso.model.ResenaCalificacion;
import com.example.edutech.curso.repository.ResenaCalificacionRepository;

@Service("resenaSecurityService") 
public class ResenaSecurityService {

    private final ResenaCalificacionRepository resenaCalificacionRepository;

    public ResenaSecurityService(ResenaCalificacionRepository resenaCalificacionRepository) {
        this.resenaCalificacionRepository = resenaCalificacionRepository;
    }

    public boolean puedeEliminarResena(Authentication authentication, Integer resenaId) {
        if (authentication == null || !authentication.isAuthenticated() || resenaId == null) {
            return false;
        }

        String usuarioRutAutenticado = authentication.getName();
        if (usuarioRutAutenticado == null) {
            return false;
        }

        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROL_ADMIN") || authority.getAuthority().equals("ROL_GERENTE_CURSOS")) {
                return true; 
            }
        }
        
        ResenaCalificacion resena = resenaCalificacionRepository.findById(resenaId).orElse(null);

        if (resena == null || resena.getUsuario() == null) {
            return false;
        }

        
        return resena.getUsuario().getRut().equals(usuarioRutAutenticado);
    }
}