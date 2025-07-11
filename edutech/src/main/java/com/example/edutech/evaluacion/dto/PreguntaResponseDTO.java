package com.example.edutech.evaluacion.dto;

public class PreguntaResponseDTO {
    private Integer id;
    private String enunciado;
    private Integer evaluacionId;

    public PreguntaResponseDTO() {
        // Constructor vacío necesario para algunos frameworks/librerías (ej. Jackson)
    }

    public PreguntaResponseDTO(Integer id, String enunciado, Integer evaluacionId) {
        this.id = id;
        this.enunciado = enunciado;
        this.evaluacionId = evaluacionId;
    }


    public Integer getId() {
        return id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public Integer getEvaluacionId() {
        return evaluacionId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setEvaluacionId(Integer evaluacionId) {
        this.evaluacionId = evaluacionId;
    }

    // Si añado la lista de respuestas:
    // public List<RespuestaResponseDTO> getRespuestas() { return respuestas; }
    // public void setRespuestas(List<RespuestaResponseDTO> respuestas) { this.respuestas = respuestas; }
}