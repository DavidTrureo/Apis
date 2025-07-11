package com.example.edutech.usuario.repository;
//REALIZADO POR: David Trureo
import com.example.edutech.usuario.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> {
}
