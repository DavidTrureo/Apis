package com.example.edutech.foro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.foro.model.MensajeForo;

@Repository
public interface MensajeForoRepository extends JpaRepository<MensajeForo, Integer> {
    List<MensajeForo> findByHiloIdOrderByFechaCreacionAsc(Integer hiloId);
}