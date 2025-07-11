package com.example.edutech.pago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.pago.model.Pago;
import com.example.edutech.usuario.model.Usuario;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByUsuario(Usuario usuario);
}
