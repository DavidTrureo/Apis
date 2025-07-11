package com.example.edutech.inscripcion.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.inscripcion.dto.InscripcionCreateDTO;
import com.example.edutech.inscripcion.dto.InscripcionResponseDTO;
import com.example.edutech.inscripcion.model.Inscripcion;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.pago.model.CuponDescuento;
import com.example.edutech.pago.model.TipoDescuento;
import com.example.edutech.pago.repository.CuponDescuentoRepository;
import com.example.edutech.pago.service.PagoService;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final CuponDescuentoRepository cuponDescuentoRepository;
    private final PagoService pagoService;

    
    public InscripcionService(InscripcionRepository inscripcionRepository,
                            UsuarioRepository usuarioRepository,
                            CursoRepository cursoRepository,
                            CuponDescuentoRepository cuponDescuentoRepository,
                            PagoService pagoService) {
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.cuponDescuentoRepository = cuponDescuentoRepository;
        this.pagoService = pagoService;
    }

    @Transactional
    public InscripcionResponseDTO registrarInscripcionConPago(InscripcionCreateDTO dto) {
        if (dto.getUsuarioRut() == null || dto.getUsuarioRut().isBlank()) {
            throw new IllegalArgumentException("Error: El RUT del usuario es obligatorio.");
        }
        if (dto.getCursoSigla() == null || dto.getCursoSigla().isBlank()) {
            throw new IllegalArgumentException("Error: La sigla del curso es obligatoria.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioRut())
            .orElseThrow(() -> new IllegalArgumentException("Error: El usuario con RUT " + dto.getUsuarioRut() + " no está registrado."));

        Curso curso = cursoRepository.findById(dto.getCursoSigla())
            .orElseThrow(() -> new IllegalArgumentException("Error: El curso con sigla " + dto.getCursoSigla() + " no está registrado."));

        if (inscripcionRepository.existsByUsuarioAndCurso(usuario, curso)) {
            throw new IllegalArgumentException("Error: Este usuario ya está inscrito en este curso.");
        }

        BigDecimal precioBaseCurso = curso.getPrecioBase();
        if (precioBaseCurso == null) {
            precioBaseCurso = BigDecimal.ZERO;
            
        }
        BigDecimal precioFinal = precioBaseCurso;
        CuponDescuento cuponAplicado = null;

        if (dto.getCodigoCupon() != null && !dto.getCodigoCupon().isBlank()) {
            cuponAplicado = cuponDescuentoRepository.findByCodigo(dto.getCodigoCupon().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Error: Cupón con código '" + dto.getCodigoCupon() + "' no encontrado."));

            if (!cuponAplicado.esValido(curso)) { 
                throw new IllegalArgumentException("Error: El cupón '" + dto.getCodigoCupon() + "' no es válido, ha expirado, ha alcanzado su límite de usos o no es aplicable a este curso.");
            }

            if (cuponAplicado.getTipoDescuento() == TipoDescuento.PORCENTAJE) {
                BigDecimal descuento = precioBaseCurso.multiply(cuponAplicado.getValorDescuento());
                precioFinal = precioBaseCurso.subtract(descuento);
            } else if (cuponAplicado.getTipoDescuento() == TipoDescuento.MONTO_FIJO) {
                precioFinal = precioBaseCurso.subtract(cuponAplicado.getValorDescuento());
            }
            precioFinal = precioFinal.max(BigDecimal.ZERO); 
        }

        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setUsuario(usuario);
        nuevaInscripcion.setCurso(curso);
        nuevaInscripcion.setFechaInscripcion(LocalDateTime.now());
        nuevaInscripcion.setPrecioPagado(precioFinal.setScale(2, RoundingMode.HALF_UP));
        nuevaInscripcion.setCuponAplicado(cuponAplicado);

        
        if (precioFinal.compareTo(BigDecimal.ZERO) > 0) {
            
            boolean pagoExitosoSimulado = true; 

            if (pagoExitosoSimulado) {
                nuevaInscripcion.setEstadoPago("PAGADO");
                nuevaInscripcion.setEstadoInscripcion("ACTIVA");
            } else {
                nuevaInscripcion.setEstadoPago("FALLIDO");
                nuevaInscripcion.setEstadoInscripcion("PENDIENTE_PAGO"); 
            }
        } else { 
            nuevaInscripcion.setEstadoPago(precioBaseCurso.compareTo(BigDecimal.ZERO) == 0 ? "GRATUITO" : "CUBIERTO_POR_CUPON");
            nuevaInscripcion.setEstadoInscripcion("ACTIVA");
        }

        Inscripcion inscripcionGuardada = inscripcionRepository.save(nuevaInscripcion);

        if ("PAGADO".equals(inscripcionGuardada.getEstadoPago()) && precioFinal.compareTo(BigDecimal.ZERO) > 0) {
            pagoService.crearRegistroPago(usuario, inscripcionGuardada, precioFinal, 
                cuponAplicado != null ? "PAGO_CON_CUPON" : "PAGO_CURSO", 
                "COMPLETADO", 
                "TRANS_" + System.currentTimeMillis() + "_" + inscripcionGuardada.getId() 
            );
        }
        
        if (cuponAplicado != null && ("PAGADO".equals(inscripcionGuardada.getEstadoPago()) || "CUBIERTO_POR_CUPON".equals(inscripcionGuardada.getEstadoPago()))) {
            cuponAplicado.incrementarUso();
            cuponDescuentoRepository.save(cuponAplicado);
        }

        return mapToResponseDTO(inscripcionGuardada);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarTodasLasInscripciones() {
        return inscripcionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // <-- ANOTACIÓN AÑADIDA
    public List<InscripcionResponseDTO> listarInscripcionesPorUsuario(String rutUsuario) {
        Usuario usuario = usuarioRepository.findById(rutUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario con RUT " + rutUsuario + " no encontrado."));
        
        return inscripcionRepository.findByUsuario(usuario).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerInscripcionPorId(Integer id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción con ID " + id + " no encontrada."));
        return mapToResponseDTO(inscripcion);
    }

    private InscripcionResponseDTO mapToResponseDTO(Inscripcion i) {
        if (i == null) return null;
        return new InscripcionResponseDTO(
                i.getId(),
                i.getUsuario() != null ? i.getUsuario().getRut() : null,
                i.getUsuario() != null ? i.getUsuario().getNombre() : null,
                i.getCurso() != null ? i.getCurso().getSigla() : null,
                i.getCurso() != null ? i.getCurso().getNombre() : null,
                i.getFechaInscripcion(),
                i.getPrecioPagado(),
                i.getCuponAplicado() != null ? i.getCuponAplicado().getCodigo() : null,
                i.getEstadoInscripcion(),
                i.getEstadoPago()
        );
    }
}