package app.brickup.apirest.exceptions;

import app.brickup.apirest.dto.Exceptions;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class Handler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Exceptions>> trataErroMethodArgument(MethodArgumentNotValidException exception) {
        List<FieldError> erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(Exceptions::new).toList());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> trataEntityNotFound() {
        return ResponseEntity.status(404).body("Tarefa não encontrada!");
    }
}
