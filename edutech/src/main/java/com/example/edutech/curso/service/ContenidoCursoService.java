package com.example.edutech.curso.service;

import java.util.List;
import java.util.stream.Collectors; // IMPORTAR

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.dto.ContenidoCursoCreateDTO;
import com.example.edutech.curso.dto.ContenidoCursoResponseDTO;
import com.example.edutech.curso.dto.ContenidoCursoUpdateDTO;
import com.example.edutech.curso.model.ContenidoCurso;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.ContenidoCursoRepository;
import com.example.edutech.curso.repository.CursoRepository; // IMPORTAR

@Service
public class ContenidoCursoService {

    private final ContenidoCursoRepository contenidoCursoRepository;
    private final CursoRepository cursoRepository;

    public ContenidoCursoService(ContenidoCursoRepository contenidoCursoRepository, CursoRepository cursoRepository) {
        this.contenidoCursoRepository = contenidoCursoRepository;
        this.cursoRepository = cursoRepository;
    }

    private ContenidoCursoResponseDTO mapToResponseDTO(ContenidoCurso contenido) {
        if (contenido == null) return null;
        return new ContenidoCursoResponseDTO(
                contenido.getId(),
                contenido.getTitulo(),
                contenido.getTipo(),
                contenido.getUrl(),
                contenido.getCurso() != null ? contenido.getCurso().getSigla() : null,
                contenido.getCurso() != null ? contenido.getCurso().getNombre() : null
        );
    }

    @Transactional(readOnly = true)
    public ContenidoCursoResponseDTO obtenerContenidoDTOPorId(int id) { // CAMBIO: Nombre y tipo de retorno
        ContenidoCurso contenido = contenidoCursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contenido con ID " + id + " no encontrado."));
        return mapToResponseDTO(contenido);
    }
    
    // Mantener el método que devuelve la entidad si es necesario internamente
    public ContenidoCurso obtenerEntidadContenidoPorId(int id) {
        return contenidoCursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contenido con ID " + id + " no encontrado."));
    }


    @Transactional(readOnly = true)
    public List<ContenidoCursoResponseDTO> listarContenidosDTO() { // CAMBIO: Nombre y tipo de retorno
        return contenidoCursoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContenidoCursoResponseDTO crearContenidoDTO(ContenidoCursoCreateDTO dto) { // CAMBIO: Nombre y tipo de retorno
        String siglaCurso = dto.getCursoSigla();
        Curso curso = cursoRepository.findById(siglaCurso)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + siglaCurso + "' no encontrado. No se puede crear contenido para un curso inexistente."));

        ContenidoCurso nuevoContenido = new ContenidoCurso();
        nuevoContenido.setTitulo(dto.getTitulo());
        nuevoContenido.setTipo(dto.getTipo());
        nuevoContenido.setUrl(dto.getUrl());
        nuevoContenido.setCurso(curso);

        ContenidoCurso guardado = contenidoCursoRepository.save(nuevoContenido);
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public ContenidoCursoResponseDTO actualizarContenidoDTO(int id, ContenidoCursoUpdateDTO dto) { // CAMBIO: Nombre y tipo de retorno
        ContenidoCurso contenidoExistente = contenidoCursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contenido con ID " + id + " no encontrado para actualizar."));

        boolean modificado = false;

        if (dto.getTitulo() != null ) {
            if(dto.getTitulo().isBlank() && !contenidoExistente.getTitulo().isBlank()){
                throw new IllegalArgumentException("El título del contenido no puede ser vacío si se desea modificar.");
            }
            if (!dto.getTitulo().equals(contenidoExistente.getTitulo())) {
                contenidoExistente.setTitulo(dto.getTitulo());
                modificado = true;
            }
        }
        if (dto.getTipo() != null) {
            if(dto.getTipo().isBlank() && !contenidoExistente.getTipo().isBlank()){
                throw new IllegalArgumentException("El tipo del contenido no puede ser vacío si se desea modificar.");
            }
            if (!dto.getTipo().equals(contenidoExistente.getTipo())) {
                contenidoExistente.setTipo(dto.getTipo());
                modificado = true;
            }
        }
        if (dto.getUrl() != null) {
            if(dto.getUrl().isBlank() && !contenidoExistente.getUrl().isBlank()){
                throw new IllegalArgumentException("La URL del contenido no puede ser vacía si se desea modificar.");
            }
            if (!dto.getUrl().equals(contenidoExistente.getUrl())) {
                contenidoExistente.setUrl(dto.getUrl());
                modificado = true;
            }
        }
        if (dto.getCursoSigla() != null && !dto.getCursoSigla().isBlank()) {
            if (contenidoExistente.getCurso() == null || !dto.getCursoSigla().equals(contenidoExistente.getCurso().getSigla())) {
                String nuevaSiglaCurso = dto.getCursoSigla();
                Curso nuevoCursoAsociado = cursoRepository.findById(nuevaSiglaCurso)
                        .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + nuevaSiglaCurso + "' no encontrado para asociar al contenido."));
                contenidoExistente.setCurso(nuevoCursoAsociado);
                modificado = true;
            }
        }

        if (modificado) {
            ContenidoCurso guardado = contenidoCursoRepository.save(contenidoExistente);
            return mapToResponseDTO(guardado);
        }
        return mapToResponseDTO(contenidoExistente);
    }

    @Transactional
    public void eliminarContenido(int id) { // CAMBIO: void, lanza excepción si no existe
        if (!contenidoCursoRepository.existsById(id)) {
            throw new IllegalArgumentException("Contenido con ID " + id + " no encontrado para eliminar.");
        }
        contenidoCursoRepository.deleteById(id);
    }
}