package com.example.edutech.pago.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.pago.model.CuponDescuento; 

@Repository
public interface CuponDescuentoRepository extends JpaRepository<CuponDescuento, Integer> {
    Optional<CuponDescuento> findByCodigo(String codigo); 
}