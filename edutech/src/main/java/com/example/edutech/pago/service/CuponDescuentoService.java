package com.example.edutech.pago.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.pago.dto.CuponDescuentoCreateDTO;
import com.example.edutech.pago.dto.CuponDescuentoDTO;
import com.example.edutech.pago.model.CuponDescuento;
import com.example.edutech.pago.model.TipoDescuento;
import com.example.edutech.pago.repository.CuponDescuentoRepository;

@Service
public class CuponDescuentoService {

    private final CuponDescuentoRepository cuponDescuentoRepository;
    private final CursoRepository cursoRepository;

    //@Autowired
    public CuponDescuentoService(CuponDescuentoRepository cuponDescuentoRepository, CursoRepository cursoRepository) {
        this.cuponDescuentoRepository = cuponDescuentoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public CuponDescuentoDTO crearCupon(CuponDescuentoCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos para crear el cupón no pueden ser nulos.");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del cupón es obligatorio.");
        }
        String codigoNormalizado = dto.getCodigo().toUpperCase();
        if (cuponDescuentoRepository.findByCodigo(codigoNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cupón con el código: " + codigoNormalizado);
        }
        if (dto.getTipoDescuento() == null) {
            throw new IllegalArgumentException("El tipo de descuento es obligatorio.");
        }
        if (dto.getValorDescuento() == null || dto.getValorDescuento().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor del descuento debe ser numérico y mayor a cero.");
        }
        if (dto.getTipoDescuento() == TipoDescuento.PORCENTAJE &&
            (dto.getValorDescuento().compareTo(BigDecimal.ZERO) <= 0 || dto.getValorDescuento().compareTo(BigDecimal.ONE) > 0)) {
            throw new IllegalArgumentException("Para descuentos por porcentaje, el valor debe estar entre 0 (exclusivo) y 1 (inclusivo para 100%). Ej: 0.10 para 10%.");
        }
        if (dto.getFechaExpiracion() == null ) {
            throw new IllegalArgumentException("La fecha de expiración es obligatoria.");
        }
        if (dto.getFechaInicioValidez() != null && dto.getFechaExpiracion().isBefore(dto.getFechaInicioValidez())) {
            throw new IllegalArgumentException("La fecha de inicio de validez no puede ser posterior a la fecha de expiración.");
        }
        // Permite que la fecha de expiración sea hoy. Si es estrictamente antes de hoy, es un error.
        if (dto.getFechaExpiracion().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de expiración no puede ser una fecha pasada.");
        }
        if (dto.getUsosMaximos() != null && dto.getUsosMaximos() < 0) {
            throw new IllegalArgumentException("Los usos máximos no pueden ser negativos.");
        }

        CuponDescuento cupon = new CuponDescuento();
        cupon.setCodigo(codigoNormalizado);
        cupon.setDescripcion(dto.getDescripcion());
        cupon.setTipoDescuento(dto.getTipoDescuento());
        cupon.setValorDescuento(dto.getValorDescuento());
        cupon.setFechaInicioValidez(dto.getFechaInicioValidez() != null ? dto.getFechaInicioValidez() : LocalDate.now());
        cupon.setFechaExpiracion(dto.getFechaExpiracion());
        cupon.setUsosMaximos(dto.getUsosMaximos());

        boolean esActivo = true;
        if (dto.getActivo() != null) {
            esActivo = dto.getActivo();
        }
        cupon.setActivo(esActivo);

        if (dto.getCursoAplicableSigla() != null && !dto.getCursoAplicableSigla().isBlank()) {
            Curso curso = cursoRepository.findById(dto.getCursoAplicableSigla())
                    .orElseThrow(() -> new IllegalArgumentException("Curso aplicable con sigla '" + dto.getCursoAplicableSigla() + "' no encontrado."));
            cupon.setCursoAplicable(curso);
        }

        CuponDescuento guardado = cuponDescuentoRepository.save(cupon);
        return mapToDTO(guardado);
    }

    public CuponDescuentoDTO obtenerCuponPorId(Integer id) {
        CuponDescuento cupon = cuponDescuentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupón con ID " + id + " no encontrado."));
        return mapToDTO(cupon);
    }

    public CuponDescuentoDTO obtenerCuponPorCodigo(String codigo) {
        CuponDescuento cupon = cuponDescuentoRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Cupón con código '" + codigo.toUpperCase() + "' no encontrado."));
        return mapToDTO(cupon);
    }

    public List<CuponDescuentoDTO> listarCupones() {
        return cuponDescuentoRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public CuponDescuentoDTO actualizarCupon(Integer id, CuponDescuentoCreateDTO dto) {
        CuponDescuento cupon = cuponDescuentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupón con ID " + id + " no encontrado para actualizar."));

        if (dto.getDescripcion() != null) {
            cupon.setDescripcion(dto.getDescripcion());
        }
        if (dto.getFechaInicioValidez() != null) {
            cupon.setFechaInicioValidez(dto.getFechaInicioValidez());
        }
        if (dto.getFechaExpiracion() != null) {
            if (cupon.getFechaInicioValidez() != null && dto.getFechaExpiracion().isBefore(cupon.getFechaInicioValidez())) {
                throw new IllegalArgumentException("La nueva fecha de expiración no puede ser anterior a la fecha de inicio de validez.");
            }
             // Permite que la fecha de expiración sea hoy. Si es estrictamente antes de hoy, es un error.
            if (dto.getFechaExpiracion().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de expiración no puede ser una fecha pasada.");
            }
            cupon.setFechaExpiracion(dto.getFechaExpiracion());
        }
        if (dto.getUsosMaximos() != null) {
            if (dto.getUsosMaximos() < 0) throw new IllegalArgumentException("Usos máximos no puede ser negativo.");
            // Si se envía 0, se interpreta como ilimitado (null)
            cupon.setUsosMaximos(dto.getUsosMaximos() == 0 ? null : dto.getUsosMaximos());
        }
        if (dto.getActivo() != null) {
            cupon.setActivo(dto.getActivo());
        }

        if (dto.getCursoAplicableSigla() != null) {
            if (dto.getCursoAplicableSigla().isBlank()) {
                cupon.setCursoAplicable(null);
            } else {
                Curso curso = cursoRepository.findById(dto.getCursoAplicableSigla())
                        .orElseThrow(() -> new IllegalArgumentException("Curso aplicable con sigla '" + dto.getCursoAplicableSigla() + "' no encontrado."));
                cupon.setCursoAplicable(curso);
            }
        }

        CuponDescuento actualizado = cuponDescuentoRepository.save(cupon);
        return mapToDTO(actualizado);
    }

    @Transactional
    public void eliminarCupon(Integer id) {
        // Primero verificar si el cupón existe
        if (!cuponDescuentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Cupón con ID " + id + " no encontrado.");
        }
        // Considerar lógica adicional aquí si es necesario, por ejemplo:
        // no permitir eliminar si el cupón ha sido usado (usosActuales > 0).
        // En ese caso, se podría solo desactivar (cupon.setActivo(false);)
        // Por ahora, simplemente lo elimina.
        cuponDescuentoRepository.deleteById(id);
    }

    private CuponDescuentoDTO mapToDTO(CuponDescuento cupon) {
        if (cupon == null) return null;
        return new CuponDescuentoDTO(
                cupon.getId(),
                cupon.getCodigo(),
                cupon.getDescripcion(),
                cupon.getTipoDescuento(),
                cupon.getValorDescuento(),
                cupon.getFechaInicioValidez(),
                cupon.getFechaExpiracion(),
                cupon.getUsosMaximos(),
                cupon.getUsosActuales(),
                cupon.getCursoAplicable() != null ? cupon.getCursoAplicable().getSigla() : null,
                cupon.isActivo()
        );
    }
}