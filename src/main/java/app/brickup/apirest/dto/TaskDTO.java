package app.brickup.apirest.dto;

import app.brickup.apirest.model.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    @NotBlank(message = "The description is required!")
    private String description;
    private Status status;
    private String image;
    private Boolean isCompleted;
}