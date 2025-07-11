package com.example.edutech.soporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.soporte.model.Soporte;
// import java.util.List; // Para futuros métodos de búsqueda
// import com.example.edutech.usuario.model.Usuario;
// import com.example.edutech.soporte.model.EstadoTicketSoporte;

@Repository
public interface SoporteRepository extends JpaRepository<Soporte, Integer> {
    // Futuros métodos personalizados si son necesarios:
    // List<Soporte> findByEstado(EstadoTicketSoporte estado);
    // List<Soporte> findByAgenteAsignado(Usuario agente);
    // List<Soporte> findByUsuarioReporta(Usuario usuario);
}