package app.brickup.apirest.dto;

import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class Exceptions {
    String field;
    String message;

    public Exceptions(FieldError fieldError) {
        this.field = fieldError.getField();
        this.message = fieldError.getDefaultMessage();
    }
}