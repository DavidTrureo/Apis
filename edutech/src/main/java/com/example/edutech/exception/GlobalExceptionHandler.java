package com.example.edutech.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger customLogger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String errorType, List<String> messages, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", errorType);
        if (messages.size() == 1) {
            body.put("message", messages.get(0));
        } else {
            body.put("messages", messages);
        }
        body.put("path", request.getDescription(false).replace("uri=", ""));

        customLogger.warn("{} - Messages: {} Path: {}", errorType, messages, body.get("path"));
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String errorType, String message, WebRequest request) {
        return buildErrorResponse(status, errorType, List.of(message), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "Campo '" + fieldError.getField() + "': " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Error de Validación", errors, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        String message = "Parámetro requerido '" + ex.getParameterName() + "' del tipo '" + ex.getParameterType() + "' no está presente.";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Parámetro Faltante", message, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        String message = "El cuerpo de la solicitud no es legible o tiene un formato JSON incorrecto. Verifique la estructura.";
        Throwable specificCause = ex.getMostSpecificCause();
        customLogger.warn("Error de parseo de JSON: {}", specificCause != null ? specificCause.getMessage() : ex.getMessage(), specificCause != null ? specificCause : ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "JSON Mal Formado", message, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NonNull HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Método HTTP '").append(ex.getMethod()).append("' no soportado para esta ruta. ");
        if (ex.getSupportedMethods() != null) {
            messageBuilder.append("Métodos soportados: ").append(String.join(", ", ex.getSupportedMethods()));
        }
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método No Permitido", messageBuilder.toString(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            @NonNull HttpMediaTypeNotSupportedException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tipo de medio '").append(ex.getContentType()).append("' no soportado. ");
        if (ex.getSupportedMediaTypes() != null && !ex.getSupportedMediaTypes().isEmpty()) {
            builder.append("Tipos de medio soportados: ").append(ex.getSupportedMediaTypes().stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de Medio No Soportado", builder.toString(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String requiredTypeSimpleName = "desconocido";
        Class<?> requiredType = ex.getRequiredType(); // Almacenar en variable para evitar advertencia
        if (requiredType != null) {
            requiredTypeSimpleName = requiredType.getSimpleName();
        }
        String message = String.format("El parámetro '%s' debería ser de tipo '%s' pero se recibió '%s'.",
                ex.getName(), requiredTypeSimpleName, ex.getValue());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Tipo de Parámetro Incorrecto", message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Solicitud Inválida", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflicto de Estado", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Error de integridad de datos. Verifique que los valores únicos no estén duplicados y que las referencias a otras entidades sean válidas.";
        Throwable rootCause = ex.getMostSpecificCause();
        String specificDetail = rootCause != null ? rootCause.getMessage() : ex.getMessage();

        if (rootCause instanceof java.sql.SQLIntegrityConstraintViolationException sqlEx) {
            specificDetail = sqlEx.getMessage();
            if (specificDetail != null) {
                if (specificDetail.toLowerCase().contains("duplicate entry")) {
                    String duplicateValueInfo = extractDuplicateValueInfo(specificDetail);
                    message = "Error: " + (duplicateValueInfo.isEmpty() ? "Un valor único ya existe." : duplicateValueInfo);
                } else if (specificDetail.toLowerCase().contains("foreign key constraint fails")) {
                    message = "Error de referencia: Una de las entidades referenciadas no existe o la operación viola una restricción de clave foránea.";
                } else if (specificDetail.toLowerCase().contains("cannot be null")){
                    message = "Error de restricción: " + extractNotNullConstraintInfo(specificDetail);
                } else {
                    message = "Error de restricción de integridad de datos. Detalles: " + specificDetail;
                }
            }
        } else if (ex.getMessage() != null) {
            if (specificDetail.toLowerCase().contains("unique constraint") || specificDetail.toLowerCase().contains("constraint violation")) {
                message = "Error de integridad: Ya existe un registro con un valor único similar o se viola una restricción.";
            }
        }
        customLogger.warn("Data Integrity Violation: Root cause - {}. Path: {}", specificDetail, request.getDescription(false), ex);
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflicto de Datos", message, request);
    }

    private String extractDuplicateValueInfo(String sqlErrorMessage) {
        try {
            if (sqlErrorMessage.contains("Duplicate entry '") && sqlErrorMessage.contains("' for key")) {
                String entryPart = sqlErrorMessage.substring(sqlErrorMessage.indexOf("Duplicate entry '") + "Duplicate entry '".length());
                String value = entryPart.substring(0, entryPart.indexOf("'"));
                String keyPart = sqlErrorMessage.substring(sqlErrorMessage.indexOf("' for key '") + "' for key '".length());
                String keyName = keyPart.substring(0, keyPart.lastIndexOf("'"));
                if (keyName.startsWith("UK_") || keyName.startsWith("UC_")) {
                    String[] parts = keyName.split("_");
                    if (parts.length > 1) {
                        keyName = parts[parts.length -1];
                    }
                } else if (keyName.contains(".")) {
                    keyName = keyName.substring(keyName.lastIndexOf(".") + 1);
                }
                return "El valor '" + value + "' ya existe para el campo '" + keyName + "'.";
            }
        } catch (Exception e) {
            customLogger.debug("No se pudo extraer información detallada de la entrada duplicada del mensaje SQL: {}", sqlErrorMessage, e);
        }
        return "Un valor único ya existe.";
    }

    private String extractNotNullConstraintInfo(String sqlErrorMessage) {
        try {
            if (sqlErrorMessage.toLowerCase().contains("column '") && sqlErrorMessage.toLowerCase().contains("' cannot be null")) {
                String columnPart = sqlErrorMessage.substring(sqlErrorMessage.toLowerCase().indexOf("column '") + "column '".length());
                String columnName = columnPart.substring(0, columnPart.indexOf("'"));
                return "El campo '" + columnName + "' no puede ser nulo.";
            }
        } catch (Exception e) {
            customLogger.debug("No se pudo extraer información de restricción NOT NULL del mensaje SQL: {}", sqlErrorMessage, e);
        }
        return "Un campo obligatorio es nulo.";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String message = "Acceso denegado. No tiene los permisos necesarios para realizar esta acción.";
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acceso Denegado", message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        String message = "Ocurrió un error inesperado en el servidor. Por favor, contacte al administrador.";
        customLogger.error("Excepción no controlada en la ruta {}: ", request.getDescription(false).replace("uri=", ""), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno del Servidor", message, request);
    }
}