package com.example.edutech.usuario.repository;
//REALIZADO POR: David Trureo
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.edutech.usuario.model.Usuario; 
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

   
    @Query("SELECT u FROM Usuario u WHERE u.rut = :rut")
    Usuario findByRut(@Param("rut") String rut); 

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = :nombreRol")
    long countByRolNombre(@Param("nombreRol") String nombreRol);
}