package com.example.edutech.usuario.service;

import java.util.Arrays;
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
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.usuario.dto.UsuarioCreateDTO;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.dto.UsuarioUpdateDTO;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.RolRepository;
import com.example.edutech.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para UsuarioService")
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private AdminRutChecker adminRutChecker;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioValido1;
    private Rol rolEstudiante;
    private Rol rolAdmin;
    private UsuarioCreateDTO dtoCreacionValido;
    private UsuarioUpdateDTO dtoUpdateValido;
    private final String RUT_ADMIN_VALIDO = "1-9";
    private final String RUT_NO_ADMIN = "2-7";


    @BeforeEach
    void setUp() {
        rolEstudiante = new Rol("ESTUDIANTE", "Rol para estudiantes");
        rolAdmin = new Rol("ADMIN", "Rol de Administrador");

        usuarioValido1 = new Usuario("11111111-1", "Usuario Uno", "uno@example.com", "Activo", "passEncriptado1", rolEstudiante);

        dtoCreacionValido = new UsuarioCreateDTO(
            "22222222-2",
            "Usuario Nuevo",
            "nuevo@example.com",
            "password123",
            "ESTUDIANTE",
            "Activo"
        );

        dtoUpdateValido = new UsuarioUpdateDTO();
        dtoUpdateValido.setNombre("Nombre Actualizado Test");
        dtoUpdateValido.setEmail("update@example.com");

        // Los stubs de adminRutChecker se configuran por test ahora
    }

    @Nested
    @DisplayName("Método: registrarUsuario")
    class RegistrarUsuarioTests {

        @Test
        @DisplayName("Debería registrar un nuevo usuario exitosamente")
        void registrarUsuario_cuandoDatosSonValidos_retornaMensajeExito() {
            when(usuarioRepository.existsById(dtoCreacionValido.getRut())).thenReturn(false);
            when(usuarioRepository.existsByEmail(dtoCreacionValido.getEmail())).thenReturn(false);
            when(rolRepository.findById("ESTUDIANTE")).thenReturn(Optional.of(rolEstudiante));
            when(passwordEncoder.encode(dtoCreacionValido.getPassword())).thenReturn("hashedPassword123");

            String resultado = usuarioService.registrarUsuario(dtoCreacionValido);

            assertTrue(resultado.contains("registrado correctamente"));
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si el RUT ya existe")
        void registrarUsuario_cuandoRutYaExiste_lanzaExcepcion() {
            when(usuarioRepository.existsById(dtoCreacionValido.getRut())).thenReturn(true);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dtoCreacionValido));

            assertTrue(exception.getMessage().contains("ya existe"));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si el Email ya existe")
        void registrarUsuario_cuandoEmailYaExiste_lanzaExcepcion() {
            when(usuarioRepository.existsById(dtoCreacionValido.getRut())).thenReturn(false);
            when(usuarioRepository.existsByEmail(dtoCreacionValido.getEmail())).thenReturn(true);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dtoCreacionValido));

            assertTrue(exception.getMessage().contains("ya está registrado"));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si el Rol no existe")
        void registrarUsuario_cuandoRolNoExiste_lanzaExcepcion() {
            dtoCreacionValido.setRolNombre("ROL_INEXISTENTE");
            when(usuarioRepository.existsById(dtoCreacionValido.getRut())).thenReturn(false);
            when(usuarioRepository.existsByEmail(dtoCreacionValido.getEmail())).thenReturn(false);
            when(rolRepository.findById("ROL_INEXISTENTE")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(dtoCreacionValido));

            assertTrue(exception.getMessage().contains("no existe"));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("Método: obtenerUsuarioDTOPorRut")
    class ObtenerUsuarioDTOPorRutTests {
        @Test
        @DisplayName("Debería devolver UsuarioDTO si el RUT existe")
        void obtenerUsuarioDTOPorRut_cuandoRutExiste_devuelveDTO() {
            when(usuarioRepository.findById("11111111-1")).thenReturn(Optional.of(usuarioValido1));

            UsuarioDTO resultado = usuarioService.obtenerUsuarioDTOPorRut("11111111-1");

            assertNotNull(resultado);
            assertEquals("11111111-1", resultado.getRut());
            assertEquals("Usuario Uno", resultado.getNombre());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si el RUT no existe")
        void obtenerUsuarioDTOPorRut_cuandoRutNoExiste_lanzaExcepcion() {
            when(usuarioRepository.findById("00000000-0")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.obtenerUsuarioDTOPorRut("00000000-0"));
            assertTrue(exception.getMessage().contains("no encontrado"));
        }
    }

    @Nested
    @DisplayName("Método: listarUsuarios")
    class ListarUsuariosTests {
        @Test
        @DisplayName("Debería devolver lista de UsuarioDTOs cuando hay usuarios")
        void listarUsuarios_cuandoHayUsuarios_devuelveLista() {
            Usuario usuario2 = new Usuario("33333333-3", "Usuario Tres", "tres@example.com", "Activo", "pass3", rolEstudiante);
            when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioValido1, usuario2));

            List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

            assertEquals(2, resultado.size());
            assertEquals("Usuario Uno", resultado.get(0).getNombre());
        }

        @Test
        @DisplayName("Debería devolver lista vacía cuando no hay usuarios")
        void listarUsuarios_cuandoNoHayUsuarios_devuelveListaVacia() {
            when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());
            List<UsuarioDTO> resultado = usuarioService.listarUsuarios();
            assertTrue(resultado.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Método: actualizarUsuario (por Admin)")
    class ActualizarUsuarioAdminTests {
        @Test
        @DisplayName("Debería actualizar usuario si solicitante es admin y datos válidos")
        void actualizarUsuario_cuandoAdminYDatosValidos_retornaMensajeExito() {
            when(adminRutChecker.isAdmin(RUT_ADMIN_VALIDO)).thenReturn(true); 
            when(usuarioRepository.findById(usuarioValido1.getRut())).thenReturn(Optional.of(usuarioValido1));
            when(usuarioRepository.findByEmail(dtoUpdateValido.getEmail())).thenReturn(Optional.empty());

            String resultado = usuarioService.actualizarUsuario(usuarioValido1.getRut(), dtoUpdateValido, RUT_ADMIN_VALIDO);

            assertEquals("Usuario actualizado correctamente.", resultado);
            verify(usuarioRepository).save(argThat(user ->
                user.getNombre().equals(dtoUpdateValido.getNombre()) &&
                user.getEmail().equals(dtoUpdateValido.getEmail())
            ));
        }

        @Test
        @DisplayName("Debería lanzar SecurityException si solicitante no es admin")
        void actualizarUsuario_cuandoNoAdmin_lanzaSecurityException() {
            when(adminRutChecker.isAdmin(RUT_NO_ADMIN)).thenReturn(false); 
            assertThrows(SecurityException.class,
                () -> usuarioService.actualizarUsuario(usuarioValido1.getRut(), dtoUpdateValido, RUT_NO_ADMIN));
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si usuario a actualizar no existe")
        void actualizarUsuario_cuandoUsuarioNoExiste_lanzaIllegalArgumentException() {
            when(adminRutChecker.isAdmin(RUT_ADMIN_VALIDO)).thenReturn(true); 
            when(usuarioRepository.findById("RUT_INEXISTENTE")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizarUsuario("RUT_INEXISTENTE", dtoUpdateValido, RUT_ADMIN_VALIDO));
        }
    }

    @Nested
    @DisplayName("Método: eliminarUsuario (por Admin)")
    class EliminarUsuarioAdminTests {
        @Test
        @DisplayName("Debería eliminar usuario si solicitante es admin y usuario existe")
        void eliminarUsuario_cuandoAdminYUsuarioExiste_retornaMensajeExito() {
            when(adminRutChecker.isAdmin(RUT_ADMIN_VALIDO)).thenReturn(true); 
            when(usuarioRepository.findById(usuarioValido1.getRut())).thenReturn(Optional.of(usuarioValido1));

            String resultado = usuarioService.eliminarUsuario(usuarioValido1.getRut(), RUT_ADMIN_VALIDO);

            assertTrue(resultado.contains("eliminados correctamente"));
            verify(inscripcionRepository).deleteByUsuarioRut(usuarioValido1.getRut());
            verify(usuarioRepository).delete(usuarioValido1);
        }
    }
    
    @Nested
    @DisplayName("Método: asignarRolAUsuario (por Admin)")
    class AsignarRolAdminTests {
        @Test
        @DisplayName("Debería asignar rol si solicitante es admin y datos válidos")
        void asignarRolAUsuario_cuandoAdminYDatosValidos_retornaMensajeExito() {
            when(adminRutChecker.isAdmin(RUT_ADMIN_VALIDO)).thenReturn(true); 
            when(usuarioRepository.findById(usuarioValido1.getRut())).thenReturn(Optional.of(usuarioValido1));
            when(rolRepository.findById("ADMIN")).thenReturn(Optional.of(rolAdmin));

            String resultado = usuarioService.asignarRolAUsuario(usuarioValido1.getRut(), "ADMIN", RUT_ADMIN_VALIDO);
            
            assertTrue(resultado.contains("asignado correctamente"));
            assertEquals(rolAdmin, usuarioValido1.getRol());
            verify(usuarioRepository).save(usuarioValido1);
        }
    }
}