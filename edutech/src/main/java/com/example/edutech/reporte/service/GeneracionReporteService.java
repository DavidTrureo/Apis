package com.example.edutech.reporte.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.evaluacion.model.EntregaEvaluacion;
import com.example.edutech.evaluacion.model.Evaluacion;
import com.example.edutech.evaluacion.repository.EntregaEvaluacionRepository;
import com.example.edutech.evaluacion.repository.EvaluacionRepository;
import com.example.edutech.inscripcion.dto.ProgresoCursoDTO;
import com.example.edutech.inscripcion.model.Inscripcion;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.inscripcion.service.ProgresoEstudianteService;
import com.example.edutech.reporte.dto.EstudianteInscritoDTO;
import com.example.edutech.reporte.dto.RendimientoEstudianteEnCursoDTO;
import com.example.edutech.reporte.dto.ReporteInscripcionesCursoDataDTO;
import com.example.edutech.reporte.dto.ReporteRendimientoCursoDataDTO;
import com.example.edutech.usuario.model.Usuario;

@Service
public class GeneracionReporteService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final ProgresoEstudianteService progresoEstudianteService;
    private final EvaluacionRepository evaluacionRepository;
    private final EntregaEvaluacionRepository entregaEvaluacionRepository;

    //@Autowired
    public GeneracionReporteService(InscripcionRepository inscripcionRepository,
                                    CursoRepository cursoRepository,
                                    ProgresoEstudianteService progresoEstudianteService,
                                    EvaluacionRepository evaluacionRepository,
                                    EntregaEvaluacionRepository entregaEvaluacionRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.progresoEstudianteService = progresoEstudianteService;
        this.evaluacionRepository = evaluacionRepository;
        this.entregaEvaluacionRepository = entregaEvaluacionRepository;
    }

    @Transactional(readOnly = true)
    public ReporteInscripcionesCursoDataDTO generarDatosReporteInscripcionesPorCurso(String cursoSigla) {
        Curso curso = cursoRepository.findById(cursoSigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + cursoSigla + "' no encontrado para generar reporte."));

        List<Inscripcion> inscripciones = inscripcionRepository.findByCurso_Sigla(cursoSigla);

        List<EstudianteInscritoDTO> estudiantesDTO = inscripciones.stream()
                .filter(inscripcion -> inscripcion.getUsuario() != null)
                .map(inscripcion -> new EstudianteInscritoDTO(
                        inscripcion.getUsuario().getRut(),
                        inscripcion.getUsuario().getNombre(),
                        inscripcion.getUsuario().getEmail(),
                        inscripcion.getFechaInscripcion(),
                        inscripcion.getPrecioPagado(),
                        inscripcion.getCuponAplicado() != null ? inscripcion.getCuponAplicado().getCodigo() : null,
                        inscripcion.getEstadoPago()
                ))
                .collect(Collectors.toList());

        BigDecimal ingresosTotalesConfirmados = inscripciones.stream()
                .filter(i -> i.getPrecioPagado() != null &&
                            ("PAGADO".equalsIgnoreCase(i.getEstadoPago()) || "CUBIERTO_POR_CUPON".equalsIgnoreCase(i.getEstadoPago()) || "GRATUITO".equalsIgnoreCase(i.getEstadoPago()))
                    )
                .map(Inscripcion::getPrecioPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReporteInscripcionesCursoDataDTO(
                curso.getSigla(),
                curso.getNombre(),
                inscripciones.size(),
                ingresosTotalesConfirmados,
                estudiantesDTO
        );
    }

    @Transactional(readOnly = true)
    public ReporteRendimientoCursoDataDTO generarDatosReporteRendimientoPorCurso(String cursoSigla) {
        Curso curso = cursoRepository.findById(cursoSigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + cursoSigla + "' no encontrado."));

        List<Inscripcion> inscripciones = inscripcionRepository.findByCurso(curso); // Obtener inscripciones del curso
        if (inscripciones.isEmpty()) {
            return new ReporteRendimientoCursoDataDTO(curso.getSigla(), curso.getNombre(), 0, new ArrayList<>());
        }

        List<Evaluacion> evaluacionesDelCurso = evaluacionRepository.findByCurso(curso); // Obtener todas las evaluaciones del curso

        List<RendimientoEstudianteEnCursoDTO> rendimientos = new ArrayList<>();
        for (Inscripcion inscripcion : inscripciones) {
            Usuario estudiante = inscripcion.getUsuario();
            if (estudiante == null) {
                continue; // Saltar si la inscripción no tiene un usuario válido
            }

            ProgresoCursoDTO progresoGeneral = progresoEstudianteService.obtenerProgresoGeneralCurso(estudiante.getRut(), curso.getSigla());
            double porcentajeProgreso = progresoGeneral.getPorcentajeCompletado();

            BigDecimal promedioCalificaciones = null;
            int evaluacionesEntregadasCorregidasCount = 0;

            if (!evaluacionesDelCurso.isEmpty()) {
                List<EntregaEvaluacion> entregasEstudiante = entregaEvaluacionRepository.findByEstudianteAndEvaluacionIn(estudiante, evaluacionesDelCurso);

                List<Double> calificaciones = entregasEstudiante.stream()
                        .filter(entrega -> "Corregida".equalsIgnoreCase(entrega.getEstado()) && entrega.getCalificacion() != null)
                        .map(EntregaEvaluacion::getCalificacion) // Asume que getCalificacion() devuelve Double
                        .collect(Collectors.toList());

                if (!calificaciones.isEmpty()) {
                    double sumaCalificaciones = calificaciones.stream().mapToDouble(Double::doubleValue).sum();
                    promedioCalificaciones = BigDecimal.valueOf(sumaCalificaciones / calificaciones.size()).setScale(2, RoundingMode.HALF_UP);
                    evaluacionesEntregadasCorregidasCount = calificaciones.size();
                }
            }

            rendimientos.add(new RendimientoEstudianteEnCursoDTO(
                    estudiante.getRut(),
                    estudiante.getNombre(),
                    porcentajeProgreso,
                    promedioCalificaciones,
                    evaluacionesEntregadasCorregidasCount,
                    evaluacionesDelCurso.size()
            ));
        }

        return new ReporteRendimientoCursoDataDTO(
                curso.getSigla(),
                curso.getNombre(),
                inscripciones.size(), // O rendimientos.size() si solo quieres contar los que tienen datos de rendimiento
                rendimientos
        );
    }
}