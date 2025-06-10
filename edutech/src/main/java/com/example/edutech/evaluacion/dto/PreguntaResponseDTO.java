package com.example.edutech.evaluacion.dto;

// No es necesario importar List aquí a menos que incluyas la lista de RespuestaResponseDTO

public class PreguntaResponseDTO {
    private Integer id;
    private String enunciado;
    private Integer evaluacionId; // ID de la evaluación a la que pertenece
    // Opcional: Si quisieras incluir las respuestas directamente en este DTO
    // import java.util.List;
    // private List<RespuestaResponseDTO> respuestas;

    public PreguntaResponseDTO() {
        // Constructor vacío necesario para algunos frameworks/librerías (ej. Jackson)
    }

    public PreguntaResponseDTO(Integer id, String enunciado, Integer evaluacionId) {
        this.id = id;
        this.enunciado = enunciado;
        this.evaluacionId = evaluacionId;
    }

    // --- Getters ---
    public Integer getId() {
        return id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public Integer getEvaluacionId() {
        return evaluacionId;
    }

    // --- Setters ---
    public void setId(Integer id) {
        this.id = id;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setEvaluacionId(Integer evaluacionId) {
        this.evaluacionId = evaluacionId;
    }

    // Si añades la lista de respuestas:
    // public List<RespuestaResponseDTO> getRespuestas() { return respuestas; }
    // public void setRespuestas(List<RespuestaResponseDTO> respuestas) { this.respuestas = respuestas; }
}