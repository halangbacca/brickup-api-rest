package app.brickup.apirest.controller;

import app.brickup.apirest.dto.TaskDTO;
import app.brickup.apirest.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {
    private final TaskService service;

    public TasksController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskDTO> listAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> findById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/image")
    public void getImageByTaskId(@PathVariable Long id, HttpServletResponse response) throws IOException {
        InputStream resource = service.getResource(id);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    @PostMapping()
    public ResponseEntity<TaskDTO> saveTask(@Valid @ModelAttribute TaskDTO dto, @RequestParam(required = false) MultipartFile image) {
        TaskDTO task = service.saveTask(dto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable @NotNull Long id, @Valid @ModelAttribute TaskDTO dto, MultipartFile image) {
        TaskDTO updatedTask = service.updateTask(dto, id, image);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskDTO> remover(@PathVariable @NotNull Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
