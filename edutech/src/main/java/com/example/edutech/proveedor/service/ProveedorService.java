package com.example.edutech.proveedor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.proveedor.dto.ProveedorDTO;
import com.example.edutech.proveedor.model.Proveedor;
import com.example.edutech.proveedor.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    //@Autowired
    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional
    public ProveedorDTO guardarProveedor(ProveedorDTO dto) {
        if (dto.getRut() == null || dto.getRut().isBlank()) {
            throw new IllegalArgumentException("El RUT del proveedor es obligatorio.");
        }
        if (proveedorRepository.existsById(dto.getRut())) {
            throw new IllegalArgumentException("Proveedor con RUT " + dto.getRut() + " ya existe.");
        }
        // Aquí podría añadir más validaciones (ej. formato de email)

        Proveedor proveedor = new Proveedor();
        proveedor.setRut(dto.getRut());
        proveedor.setNombre(dto.getNombre());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedor.setMail(dto.getMail());

        Proveedor guardado = proveedorRepository.save(proveedor);
        return mapToDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarProveedores() {
        return proveedorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProveedorDTO obtenerProveedorPorRut(String rut) {
        Proveedor proveedor = proveedorRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor con RUT " + rut + " no encontrado."));
        return mapToDTO(proveedor);
    }

    @Transactional
    public ProveedorDTO actualizarProveedor(String rut, ProveedorDTO dto) {
        Proveedor proveedorExistente = proveedorRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor con RUT " + rut + " no encontrado para actualizar."));

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            proveedorExistente.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) { // Permite descripción vacía si se desea
            proveedorExistente.setDescripcion(dto.getDescripcion());
        }
        if (dto.getMail() != null && !dto.getMail().isBlank()) {
            // Aquí podría validar el formato del email
            proveedorExistente.setMail(dto.getMail());
        }

        Proveedor actualizado = proveedorRepository.save(proveedorExistente);
        return mapToDTO(actualizado);
    }

    @Transactional
    public void eliminarProveedor(String rut) {
        if (!proveedorRepository.existsById(rut)) {
            throw new IllegalArgumentException("Proveedor con RUT " + rut + " no encontrado para eliminar.");
        }
        // Considerar si hay dependencias antes de eliminar (ej. si está asociado a algún servicio activo)
        proveedorRepository.deleteById(rut);
    }

    private ProveedorDTO mapToDTO(Proveedor proveedor) {
        if (proveedor == null) return null;
        return new ProveedorDTO(
                proveedor.getRut(),
                proveedor.getNombre(),
                proveedor.getDescripcion(),
                proveedor.getMail()
        );
    }
}