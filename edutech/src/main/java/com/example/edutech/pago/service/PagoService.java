package com.example.edutech.pago.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.inscripcion.model.Inscripcion;
import com.example.edutech.pago.dto.PagoResponseDTO;
import com.example.edutech.pago.model.Pago;
import com.example.edutech.pago.repository.PagoRepository;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoService(PagoRepository pagoRepository, UsuarioRepository usuarioRepository) {
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Pago crearRegistroPago(Usuario usuario, Inscripcion inscripcion, BigDecimal monto, String metodoPago, String estadoPago, String idTransaccionExterna) {
        if (usuario == null || inscripcion == null || monto == null || metodoPago == null || estadoPago == null) {
            logger.error("Argumentos nulos al intentar crear registro de pago. Usuario: {}, Inscripcion: {}, Monto: {}, Metodo: {}, Estado: {}",
                usuario, inscripcion, monto, metodoPago, estadoPago);
            throw new IllegalArgumentException("Todos los argumentos para crear un pago son requeridos y no pueden ser nulos.");
        }
        Pago nuevoPago = new Pago();
        nuevoPago.setUsuario(usuario);
        nuevoPago.setInscripcion(inscripcion);
        nuevoPago.setMonto(monto);
        nuevoPago.setMetodoPago(metodoPago);
        nuevoPago.setFechaPago(LocalDateTime.now());
        nuevoPago.setEstadoPago(estadoPago);
        nuevoPago.setIdTransaccionProveedor(idTransaccionExterna != null ? idTransaccionExterna : "SIM_" + System.currentTimeMillis());
        logger.info("Creando registro de pago para usuario {} e inscripción {}", usuario.getRut(), inscripcion.getId());
        return pagoRepository.save(nuevoPago);
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPagoPorId(Integer id) {
        logger.debug("Buscando pago con ID: {}", id);
        Pago pago = pagoRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Pago con ID {} no encontrado.", id);
                return new IllegalArgumentException("Pago con ID " + id + " no encontrado.");
            });
        return mapToDTO(pago);
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPagosPorUsuario(String usuarioRut) {
        logger.debug("Listando pagos para usuario RUT: {}", usuarioRut);
        Usuario usuario = usuarioRepository.findById(usuarioRut)
            .orElseThrow(() -> {
                logger.warn("Usuario con RUT {} no encontrado al listar pagos.", usuarioRut);
                return new IllegalArgumentException("Usuario con RUT " + usuarioRut + " no encontrado.");
            });
        List<Pago> pagos = pagoRepository.findByUsuario(usuario);
        logger.info("Encontrados {} pagos para el usuario {}", pagos.size(), usuarioRut);
        return pagos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarTodosLosPagos() {
        logger.debug("Listando todos los pagos.");
        List<Pago> pagos = pagoRepository.findAll();
        logger.info("Total de pagos encontrados: {}.", pagos.size());
        return pagos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PagoResponseDTO mapToDTO(Pago pago) {
        if (pago == null) return null;
        return new PagoResponseDTO(
            pago.getId(),
            pago.getUsuario() != null ? pago.getUsuario().getRut() : null,
            pago.getInscripcion() != null ? pago.getInscripcion().getId() : null,
            pago.getMonto(),
            pago.getMetodoPago(),
            pago.getFechaPago(),
            pago.getEstadoPago(),
            pago.getIdTransaccionProveedor()
        );
    }
}