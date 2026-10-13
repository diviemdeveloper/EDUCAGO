package es.safareyes.educago.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 NOT FOUND
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail handleNotFound(RecursoNoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 422 UNPROCESSABLE ENTITY (Reglas de negocio)
    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail handleReglaNegocio(ReglaNegocioException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Violación de regla de negocio");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 409 CONFLICT (Duplicados, solapamientos)
    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail handleConflicto(ConflictoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflicto de recursos");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 403 FORBIDDEN (Acceso denegado a recurso ajeno o falta de rol)
    @ExceptionHandler(AccesoDenegadoException.class)
    public ProblemDetail handleAccesoDenegado(AccesoDenegadoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Acceso denegado");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 400 BAD REQUEST (Errores de validación DTO con @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "La petición contiene errores de validación");
        problem.setTitle("Error de validación de entrada");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("errores", errores);
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 500 INTERNAL SERVER ERROR (Errores no controlados)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Error interno del servidor");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
}