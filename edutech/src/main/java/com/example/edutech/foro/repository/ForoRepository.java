package com.example.edutech.foro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.foro.model.Foro;

@Repository
public interface ForoRepository extends JpaRepository<Foro, Integer> {
    Optional<Foro> findByCursoSigla(String cursoSigla);
    List<Foro> findByTituloContainingIgnoreCase(String titulo);
}