package com.example.edutech.evaluacion.dto;

public class RespuestaResponseDTO {
    private Integer id;
    private String contenido;
    private boolean esCorrecta;
    private Integer preguntaId;
    // Opcional: podría incluir el enunciado de la pregunta si es útil
    // private String preguntaEnunciado;

    public RespuestaResponseDTO() {}

    public RespuestaResponseDTO(Integer id, String contenido, boolean esCorrecta, Integer preguntaId) {
        this.id = id;
        this.contenido = contenido;
        this.esCorrecta = esCorrecta;
        this.preguntaId = preguntaId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public boolean isEsCorrecta() { return esCorrecta; }
    public void setEsCorrecta(boolean esCorrecta) { this.esCorrecta = esCorrecta; }
    public Integer getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Integer preguntaId) { this.preguntaId = preguntaId; }
}