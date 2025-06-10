package com.example.edutech.foro.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.foro.dto.ForoCreateDTO;
import com.example.edutech.foro.dto.ForoResponseDTO;
import com.example.edutech.foro.service.ForoService;

@RestController
@RequestMapping("/api/foros")
public class ForoController {

    private static final Logger logger = LoggerFactory.getLogger(ForoController.class);
    private final ForoService foroService;

    public ForoController(ForoService foroService) {
        this.foroService = foroService;
    }

    // Nota: La creación de foros podría ser automática al crear un curso.
    // Este endpoint es para creación manual si es necesario.
    @PostMapping
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_GERENTE_CURSOS')")
    public ResponseEntity<?> crearForo(@RequestBody ForoCreateDTO createDTO) {
        try {
            ForoResponseDTO nuevoForo = foroService.crearForo(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoForo);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear foro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear foro:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el foro.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerForoPorId(@PathVariable Integer id) {
        try {
            ForoResponseDTO foro = foroService.obtenerForoPorId(id);
            return ResponseEntity.ok(foro);
        } catch (IllegalArgumentException e) {
            logger.warn("Foro con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/curso/{cursoSigla}")
    public ResponseEntity<?> obtenerForoPorCursoSigla(@PathVariable String cursoSigla) {
        try {
            ForoResponseDTO foro = foroService.obtenerForoPorCursoSigla(cursoSigla);
            return ResponseEntity.ok(foro);
        } catch (IllegalArgumentException e) {
            logger.warn("Foro para curso {} no encontrado: {}", cursoSigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ForoResponseDTO>> listarTodosLosForos() {
        List<ForoResponseDTO> foros = foroService.listarTodosLosForos();
        return ResponseEntity.ok(foros);
    }
}