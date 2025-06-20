// Ubicación: src/test/java/com/example/edutech/curso/service/CursoServiceTest.java
package com.example.edutech.curso.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.curso.dto.CursoCreateDTO;
import com.example.edutech.curso.dto.CursoResponseDTO;
import com.example.edutech.curso.model.ContenidoCurso;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.EstadoCursoEnum;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para CursoService")
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private AdminRutChecker adminRutChecker;

    @InjectMocks
    private CursoService cursoService;

    // --- Datos de prueba ---
    private final String RUT_ADMIN = "11111111-1";
    private final String RUT_NO_ADMIN = "22222222-2";
    private final String RUT_INSTRUCTOR = "33333333-3";
    private final String SIGLA_CURSO = "PROG101";

    private Usuario instructor;
    private Curso cursoBorrador;
    private CursoCreateDTO cursoCreateDTO;

    @BeforeEach
    void setUp() {
        instructor = new Usuario(RUT_INSTRUCTOR, "Instructor Pro", "instructor@test.com", "Activo", "pass", new Rol("INSTRUCTOR", ""));

        cursoBorrador = new Curso();
        cursoBorrador.setSigla(SIGLA_CURSO);
        cursoBorrador.setNombre("Programación Básica");
        cursoBorrador.setEstadoCurso(EstadoCursoEnum.BORRADOR);
        cursoBorrador.setPrecioBase(new BigDecimal("100.00"));
        cursoBorrador.setContenidos(new ArrayList<>()); // Inicializar la lista para evitar NullPointerException

        cursoCreateDTO = new CursoCreateDTO();
        cursoCreateDTO.setSigla("NEW101");
        cursoCreateDTO.setNombre("Nuevo Curso de Prueba");
        cursoCreateDTO.setDescripcionDetallada("Descripción detallada.");
        cursoCreateDTO.setCategoria("Tecnología");
    }

    @Nested
    @DisplayName("Creación de Cursos")
    class CrearCursoTests {

        @Test
        @DisplayName("Debería crear un curso exitosamente si el solicitante es admin")
        void crearCurso_conAdminValido_deberiaSerExitoso() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_ADMIN)).thenReturn(true);
            when(cursoRepository.existsById(anyString())).thenReturn(false);
            when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            CursoResponseDTO resultado = cursoService.crearCurso(cursoCreateDTO, RUT_ADMIN);

            // Assert
            assertNotNull(resultado);
            assertEquals(cursoCreateDTO.getSigla(), resultado.getSigla());
        }

        @Test
        @DisplayName("Debería lanzar SecurityException si el solicitante NO es admin")
        void crearCurso_sinSerAdmin_deberiaLanzarSecurityException() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_NO_ADMIN)).thenReturn(false);

            // Act & Assert
            // SOLUCIÓN a "Throwable method result is ignored"
            SecurityException exception = assertThrows(SecurityException.class, () -> cursoService.crearCurso(cursoCreateDTO, RUT_NO_ADMIN));
            assertNotNull(exception); // Opcional, pero buena práctica
            
            verify(cursoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si la sigla ya existe")
        void crearCurso_conSiglaDuplicada_deberiaLanzarIllegalArgumentException() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_ADMIN)).thenReturn(true);
            when(cursoRepository.existsById(cursoCreateDTO.getSigla())).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cursoService.crearCurso(cursoCreateDTO, RUT_ADMIN));
            assertTrue(exception.getMessage().contains("Ya existe un curso con la sigla"));
        }
    }

    @Nested
    @DisplayName("Actualización de Estado de Cursos")
    class ActualizarEstadoCursoTests {

        @Test
        @DisplayName("Debería lanzar IllegalStateException al intentar publicar un curso sin instructor")
        void actualizarEstado_aPublicadoSinInstructor_deberiaLanzarIllegalStateException() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_ADMIN)).thenReturn(true);
            when(cursoRepository.findById(SIGLA_CURSO)).thenReturn(Optional.of(cursoBorrador));
            cursoBorrador.setInstructor(null);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cursoService.actualizarEstadoCurso(SIGLA_CURSO, EstadoCursoEnum.PUBLICADO, RUT_ADMIN));
            assertEquals("No se puede publicar un curso sin instructor asignado.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería lanzar IllegalStateException al intentar publicar un curso sin contenido")
        void actualizarEstado_aPublicadoSinContenido_deberiaLanzarIllegalStateException() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_ADMIN)).thenReturn(true);
            when(cursoRepository.findById(SIGLA_CURSO)).thenReturn(Optional.of(cursoBorrador));
            cursoBorrador.setInstructor(instructor);
            cursoBorrador.setContenidos(Collections.emptyList());

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cursoService.actualizarEstadoCurso(SIGLA_CURSO, EstadoCursoEnum.PUBLICADO, RUT_ADMIN));
            assertEquals("No se puede publicar un curso sin contenido.", exception.getMessage());
        }

        @Test
        @DisplayName("Debería publicar un curso exitosamente si cumple las condiciones")
        void actualizarEstado_aPublicadoConCondicionesOK_deberiaSerExitoso() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_ADMIN)).thenReturn(true);
            when(cursoRepository.findById(SIGLA_CURSO)).thenReturn(Optional.of(cursoBorrador));
            when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

            cursoBorrador.setInstructor(instructor);
            cursoBorrador.getContenidos().add(new ContenidoCurso("Intro", "Video", "url", cursoBorrador));

            // Act
            CursoResponseDTO resultado = cursoService.actualizarEstadoCurso(SIGLA_CURSO, EstadoCursoEnum.PUBLICADO, RUT_ADMIN);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoCursoEnum.PUBLICADO, resultado.getEstadoCurso());
            verify(cursoRepository).save(cursoBorrador);
        }
    }

    @Nested
    @DisplayName("Búsqueda y Listado de Cursos")
    class BuscarCursosTests {
        @Test
        @DisplayName("Debería devolver una página de cursos al buscar con filtros")
        void buscarCursos_conFiltros_deberiaDevolverPaginaDeCursos() {
            // Arrange
            List<Curso> listaCursos = List.of(cursoBorrador);
            Page<Curso> paginaCursos = new PageImpl<>(listaCursos);
            // SOLUCIÓN a Type Safety warning: Usar any() en lugar de any(Specification.class)
            when(cursoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(paginaCursos);

            // Act
            Page<CursoResponseDTO> resultado = cursoService.buscarCursosConFiltros("programación", null, null, null, null, null, Pageable.unpaged());

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            assertEquals("Programación Básica", resultado.getContent().get(0).getNombre());
        }
    }
    
    @Nested
    @DisplayName("Eliminación de Cursos")
    class EliminarCursoTests {
    
        @Test
        @DisplayName("Debería eliminar un curso y sus inscripciones si es admin")
        void eliminarCurso_siendoAdmin_deberiaSerExitoso() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_ADMIN)).thenReturn(true);
            when(cursoRepository.findById(SIGLA_CURSO)).thenReturn(Optional.of(cursoBorrador));

            // Act
            String mensaje = cursoService.eliminarCurso(SIGLA_CURSO, RUT_ADMIN);

            // Assert
            assertTrue(mensaje.contains("eliminados correctamente"));
            verify(inscripcionRepository).deleteByCurso(SIGLA_CURSO);
            verify(cursoRepository).delete(cursoBorrador);
        }
        
        @Test
        @DisplayName("Debería lanzar SecurityException al intentar eliminar sin ser admin")
        void eliminarCurso_sinSerAdmin_deberiaLanzarSecurityException() {
            // Arrange
            when(adminRutChecker.isAdmin(RUT_NO_ADMIN)).thenReturn(false);

            // Act & Assert
            SecurityException exception = assertThrows(SecurityException.class, () -> cursoService.eliminarCurso(SIGLA_CURSO, RUT_NO_ADMIN));
            assertNotNull(exception); // Verifica que la excepción fue lanzada

            // SOLUCIÓN al error de ambigüedad
            verify(inscripcionRepository, never()).deleteByCurso(anyString());
            verify(cursoRepository, never()).delete(any(Curso.class));
        }
    }
}