package com.example.edutech.curso.model;

public enum EstadoCursoEnum {
    BORRADOR("Borrador"), // Curso en creación, no visible para estudiantes
    EN_REVISION("En Revisión"), // Contenido listo para ser revisado por el Gerente de Cursos
    PUBLICADO("Publicado"), // Visible y accesible para inscripciones y estudiantes
    ARCHIVADO("Archivado"); // Ya no se ofrecen nuevas inscripciones, pero los inscritos pueden acceder (opcional)

    private final String descripcion;

    EstadoCursoEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    // Opcional: método para convertir String a Enum de forma segura
    public static EstadoCursoEnum fromString(String text) {
        if (text != null) {
            for (EstadoCursoEnum b : EstadoCursoEnum.values()) {
                if (text.equalsIgnoreCase(b.descripcion) || text.equalsIgnoreCase(b.name())) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("No se encontró un estado de curso para el texto: " + text);
    }
}