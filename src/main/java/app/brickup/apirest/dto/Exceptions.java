package app.brickup.apirest.dto;

import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class Exceptions {
    String campo;
    String mensage;

    public Exceptions(FieldError fieldError) {
        this.campo = fieldError.getField();
        this.mensage = fieldError.getDefaultMessage();
    }
}