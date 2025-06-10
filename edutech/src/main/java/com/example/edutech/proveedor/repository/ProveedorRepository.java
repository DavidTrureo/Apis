package com.example.edutech.proveedor.repository;

import com.example.edutech.proveedor.model.Proveedor; // Cambiar import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, String> { // Cambiar Proveedor
}