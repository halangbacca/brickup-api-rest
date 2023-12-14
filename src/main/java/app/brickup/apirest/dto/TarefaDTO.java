package app.brickup.apirest.dto;

import app.brickup.apirest.model.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarefaDTO {
    private Long id;
    @NotBlank(message = "A descrição é obrigatória!")
    private String descricao;
    private Status status;
    private String imagem;
    private Boolean finalizado;
}