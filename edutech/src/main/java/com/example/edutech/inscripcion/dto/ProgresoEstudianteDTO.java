package com.example.edutech.inscripcion.dto;
//REALIZADO POR: Cristóbal Mira
import java.time.LocalDateTime;

public class ProgresoEstudianteDTO {
    private Integer id;
    private Integer inscripcionId;
    private String rutEstudiante;
    private String nombreEstudiante;
    private String siglaCurso;
    private String nombreCurso;
    private Integer contenidoCursoId;
    private String tituloContenidoCurso;
    private boolean completado;
    private LocalDateTime fechaCompletado;
    private LocalDateTime fechaUltimaActualizacion;

    public ProgresoEstudianteDTO() {}

    public ProgresoEstudianteDTO(Integer id, Integer inscripcionId, String rutEstudiante, String nombreEstudiante, String siglaCurso, String nombreCurso, Integer contenidoCursoId, String tituloContenidoCurso, boolean completado, LocalDateTime fechaCompletado, LocalDateTime fechaUltimaActualizacion) {
        this.id = id;
        this.inscripcionId = inscripcionId;
        this.rutEstudiante = rutEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.siglaCurso = siglaCurso;
        this.nombreCurso = nombreCurso;
        this.contenidoCursoId = contenidoCursoId;
        this.tituloContenidoCurso = tituloContenidoCurso;
        this.completado = completado;
        this.fechaCompletado = fechaCompletado;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Integer inscripcionId) { this.inscripcionId = inscripcionId; }
    public String getRutEstudiante() { return rutEstudiante; }
    public void setRutEstudiante(String rutEstudiante) { this.rutEstudiante = rutEstudiante; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }
    public String getSiglaCurso() { return siglaCurso; }
    public void setSiglaCurso(String siglaCurso) { this.siglaCurso = siglaCurso; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public Integer getContenidoCursoId() { return contenidoCursoId; }
    public void setContenidoCursoId(Integer contenidoCursoId) { this.contenidoCursoId = contenidoCursoId; }
    public String getTituloContenidoCurso() { return tituloContenidoCurso; }
    public void setTituloContenidoCurso(String tituloContenidoCurso) { this.tituloContenidoCurso = tituloContenidoCurso; }
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
    public LocalDateTime getFechaCompletado() { return fechaCompletado; }
    public void setFechaCompletado(LocalDateTime fechaCompletado) { this.fechaCompletado = fechaCompletado; }
    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) { this.fechaUltimaActualizacion = fechaUltimaActualizacion; }
}