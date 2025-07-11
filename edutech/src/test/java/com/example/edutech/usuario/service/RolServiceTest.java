package com.example.edutech.usuario.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.usuario.dto.RolCreateDTO;
import com.example.edutech.usuario.dto.RolDTO;
import com.example.edutech.usuario.dto.RolUpdateDTO;
import com.example.edutech.usuario.model.Permiso;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.repository.RolRepository;
import com.example.edutech.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para RolService")
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdminRutChecker adminRutChecker;

    @InjectMocks
    private RolService rolService;

    private RolCreateDTO rolCreateDTOValido;
    private RolUpdateDTO rolUpdateDTOValido;
    private Rol rolExistente; // Este será el objeto que se usa para mockear findById
    private Rol rolAdminEntidad;
    private final String ADMIN_RUT_VALIDO = "ADMIN_RUT_OK";
    private final String ADMIN_RUT_INVALIDO = "USER_RUT_NOADMIN";

    @BeforeEach
    void setUp() {
        rolCreateDTOValido = new RolCreateDTO("ROL_PARA_CREAR", "Descripción del rol a crear");
        rolUpdateDTOValido = new RolUpdateDTO("Descripción actualizada del rol");

        // Usar "ROL_EXISTENTE" consistentemente
        rolExistente = new Rol("ROL_EXISTENTE", "Descripción original");
        Permiso permisoVer = new Permiso("VER_RECURSO", "Permite ver recursos");
        permisoVer.setId(1);
        Set<Permiso> permisos = new HashSet<>();
        permisos.add(permisoVer);
        rolExistente.setPermisos(permisos); // Asegurar que el set de permisos no sea nulo

        rolAdminEntidad = new Rol("ADMIN", "Rol de administrador del sistema");
        rolAdminEntidad.setPermisos(new HashSet<>()); // Asegurar que el set de permisos no sea nulo
    }

    @Nested
    @DisplayName("Método: crearRol")
    class CrearRolTests {
        @Test
        @DisplayName("Debería crear rol exitosamente si admin es válido y rol no existe")
        void crearRol_cuandoAdminValidoYRolNoExiste_retornaRolDTO() {
            when(adminRutChecker.isAdmin(ADMIN_RUT_VALIDO)).thenReturn(true);
            when(rolRepository.existsById("ROL_PARA_CREAR")).thenReturn(false);
            when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> {
                Rol r = invocation.getArgument(0);
                r.setPermisos(new HashSet<>()); // Un rol nuevo no tiene permisos
                return r;
            });

            RolDTO resultado = rolService.crearRol(rolCreateDTOValido, ADMIN_RUT_VALIDO);

            assertNotNull(resultado);
            assertEquals("ROL_PARA_CREAR", resultado.getNombre());
            assertEquals("Descripción del rol a crear", resultado.getDescripcion());
            assertTrue(resultado.getPermisos().isEmpty());
            verify(rolRepository).save(any(Rol.class));
        }

        @Test
        @DisplayName("Debería lanzar SecurityException si el solicitante no es admin")
        void crearRol_cuandoSolicitanteNoEsAdmin_lanzaSecurityException() {
            when(adminRutChecker.isAdmin(ADMIN_RUT_INVALIDO)).thenReturn(false);

            SecurityException exception = assertThrows(SecurityException.class,
                () -> rolService.crearRol(rolCreateDTOValido, ADMIN_RUT_INVALIDO));
            assertEquals("Acción no autorizada: Se requieren privilegios de administrador para crear un rol.", exception.getMessage());
            verify(rolRepository, never()).save(any(Rol.class));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si el nombre del rol ya existe")
        void crearRol_cuandoNombreRolYaExiste_lanzaIllegalArgumentException() {
            when(adminRutChecker.isAdmin(ADMIN_RUT_VALIDO)).thenReturn(true);
            when(rolRepository.existsById("ROL_PARA_CREAR")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rolService.crearRol(rolCreateDTOValido, ADMIN_RUT_VALIDO));
            assertEquals("El rol 'ROL_PARA_CREAR' ya existe.", exception.getMessage());
            verify(rolRepository, never()).save(any(Rol.class));
        }
    }

    @Nested
    @DisplayName("Método: listarRoles")
    class ListarRolesTests {
        @Test
        @DisplayName("Debería devolver lista de RolDTOs cuando hay roles")
        void listarRoles_cuandoHayRoles_devuelveListaRolDTO() {
            when(rolRepository.findAll()).thenReturn(Arrays.asList(rolExistente, rolAdminEntidad));

            List<RolDTO> resultado = rolService.listarRoles();

            assertEquals(2, resultado.size());
            assertEquals("ROL_EXISTENTE", resultado.get(0).getNombre());
            assertEquals(1, resultado.get(0).getPermisos().size());
            assertTrue(resultado.get(0).getPermisos().contains("VER_RECURSO"));
            assertEquals("ADMIN", resultado.get(1).getNombre());
        }
    }
    
    @Nested
    @DisplayName("Método: obtenerRolDTOPorNombre")
    class ObtenerRolDTOPorNombreTests {
        @Test
        @DisplayName("Debería devolver RolDTO si el rol existe")
        void obtenerRolDTOPorNombre_cuandoRolExiste_devuelveRolDTO() {
            // El rolExistente fue inicializado en setUp con el nombre "ROL_EXISTENTE"
            when(rolRepository.findById("ROL_EXISTENTE")).thenReturn(Optional.of(rolExistente));
            
            RolDTO resultado = rolService.obtenerRolDTOPorNombre("ROL_EXISTENTE");
            
            assertNotNull(resultado);
            assertEquals("ROL_EXISTENTE", resultado.getNombre());
            assertEquals(1, resultado.getPermisos().size());
            assertTrue(resultado.getPermisos().contains("VER_RECURSO"));
        }

        @Test
        @DisplayName("Debería lanzar IllegalArgumentException si el rol no existe")
        void obtenerRolDTOPorNombre_cuandoRolNoExiste_lanzaExcepcion() {
            when(rolRepository.findById("ROL_FANTASMA")).thenReturn(Optional.empty());
            assertThrows(IllegalArgumentException.class, () -> rolService.obtenerRolDTOPorNombre("ROL_FANTASMA"));
        }
    }

    @Nested
    @DisplayName("Método: actualizarRol")
    class ActualizarRolTests {
        @Test
        @DisplayName("Debería actualizar descripción si admin es válido y rol existe")
        void actualizarRol_cuandoAdminValidoYRolExiste_retornaRolDTOActualizado() {
            when(adminRutChecker.isAdmin(ADMIN_RUT_VALIDO)).thenReturn(true);
            when(rolRepository.findById("ROL_EXISTENTE")).thenReturn(Optional.of(rolExistente));
            // CORRECCIÓN: Hacer que save devuelva la instancia modificada
            when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

            RolDTO resultado = rolService.actualizarRol("ROL_EXISTENTE", rolUpdateDTOValido, ADMIN_RUT_VALIDO);

            assertNotNull(resultado);
            assertEquals("Descripción actualizada del rol", resultado.getDescripcion());
            assertEquals("ROL_EXISTENTE", resultado.getNombre());
            verify(rolRepository).save(rolExistente);
            assertEquals("Descripción actualizada del rol", rolExistente.getDescripcion());
        }

        @Test
        @DisplayName("Debería no hacer cambios si la descripción es la misma")
        void actualizarRol_cuandoDescripcionEsLaMisma_noGuardaYRetornaDTO() {
            RolUpdateDTO dtoSinCambios = new RolUpdateDTO("Descripción original"); // Misma descripción que rolExistente
            when(adminRutChecker.isAdmin(ADMIN_RUT_VALIDO)).thenReturn(true);
            when(rolRepository.findById("ROL_EXISTENTE")).thenReturn(Optional.of(rolExistente));

            RolDTO resultado = rolService.actualizarRol("ROL_EXISTENTE", dtoSinCambios, ADMIN_RUT_VALIDO);
            
            assertNotNull(resultado);
            assertEquals("Descripción original", resultado.getDescripcion());
            verify(rolRepository, never()).save(any(Rol.class));
        }
    }
    
    @Nested
    @DisplayName("Método: eliminarRol")
    class EliminarRolTests {
        @Test
        @DisplayName("Debería eliminar rol si admin es válido, rol existe y no está en uso")
        void eliminarRol_cuandoValidoYNoEnUso_retornaMensajeExito() {
            Rol rolAEliminar = new Rol("ROL_A_BORRAR", "Se va a borrar");
            when(adminRutChecker.isAdmin(ADMIN_RUT_VALIDO)).thenReturn(true);
            when(rolRepository.findById("ROL_A_BORRAR")).thenReturn(Optional.of(rolAEliminar));
            when(usuarioRepository.countByRolNombre("ROL_A_BORRAR")).thenReturn(0L);

            String resultado = rolService.eliminarRol("ROL_A_BORRAR", ADMIN_RUT_VALIDO);
            
            assertTrue(resultado.contains("eliminado correctamente"));
            verify(rolRepository).delete(rolAEliminar);
        }

        @Test
        @DisplayName("Debería lanzar IllegalStateException si rol está en uso por usuarios")
        void eliminarRol_cuandoRolEstaEnUso_lanzaIllegalStateException() {
            when(adminRutChecker.isAdmin(ADMIN_RUT_VALIDO)).thenReturn(true);
            when(rolRepository.findById("ROL_EXISTENTE")).thenReturn(Optional.of(rolExistente));
            when(usuarioRepository.countByRolNombre("ROL_EXISTENTE")).thenReturn(5L);

            assertThrows(IllegalStateException.class, 
                () -> rolService.eliminarRol("ROL_EXISTENTE", ADMIN_RUT_VALIDO));
            verify(rolRepository, never()).delete(any(Rol.class));
        }
    }
}