package com.example.edutech.foro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.dto.CursoDTO;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.foro.dto.ForoCreateDTO;
import com.example.edutech.foro.dto.ForoResponseDTO;
import com.example.edutech.foro.model.Foro;
import com.example.edutech.foro.repository.ForoRepository;

@Service
public class ForoService {

    private final ForoRepository foroRepository;
    private final CursoRepository cursoRepository;

    public ForoService(ForoRepository foroRepository, CursoRepository cursoRepository) {
        this.foroRepository = foroRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public ForoResponseDTO crearForo(ForoCreateDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título del foro es obligatorio.");
        }
        if (dto.getCursoSigla() == null || dto.getCursoSigla().isBlank()) {
            throw new IllegalArgumentException("La sigla del curso es obligatoria para crear un foro.");
        }

        Curso curso = cursoRepository.findById(dto.getCursoSigla())
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + dto.getCursoSigla() + "' no encontrado."));

        if (foroRepository.findByCursoSigla(dto.getCursoSigla()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un foro para el curso con sigla '" + dto.getCursoSigla() + "'.");
        }

        Foro foro = new Foro();
        foro.setTitulo(dto.getTitulo());
        foro.setDescripcion(dto.getDescripcion());
        foro.setCurso(curso);
        // fechaCreacion se establece en el constructor de Foro

        Foro foroGuardado = foroRepository.save(foro);
        return mapToForoResponseDTO(foroGuardado);
    }

    @Transactional(readOnly = true)
    public ForoResponseDTO obtenerForoPorId(Integer id) {
        Foro foro = foroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foro con ID " + id + " no encontrado."));
        return mapToForoResponseDTO(foro);
    }

    @Transactional(readOnly = true)
    public ForoResponseDTO obtenerForoPorCursoSigla(String cursoSigla) {
        Foro foro = foroRepository.findByCursoSigla(cursoSigla)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un foro para el curso con sigla '" + cursoSigla + "'."));
        return mapToForoResponseDTO(foro);
    }
    
    @Transactional(readOnly = true)
    public List<ForoResponseDTO> listarTodosLosForos() {
        return foroRepository.findAll().stream()
                .map(this::mapToForoResponseDTO)
                .collect(Collectors.toList());
    }

    // Considerar añadir métodos para actualizar y eliminar foros si es necesario.
    // Por lo general, un foro se elimina si se elimina el curso.

    private ForoResponseDTO mapToForoResponseDTO(Foro foro) {
        if (foro == null) return null;
        CursoDTO cursoDTO = null;
        if (foro.getCurso() != null) {
            cursoDTO = new CursoDTO(foro.getCurso().getSigla(), foro.getCurso().getNombre());
        }
        return new ForoResponseDTO(
                foro.getId(),
                foro.getTitulo(),
                foro.getDescripcion(),
                foro.getFechaCreacion(),
                cursoDTO,
                foro.getHilos() != null ? foro.getHilos().size() : 0
        );
    }
}