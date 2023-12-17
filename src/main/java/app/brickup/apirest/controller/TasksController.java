package app.brickup.apirest.controller;

import app.brickup.apirest.dto.TaskDTO;
import app.brickup.apirest.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<TaskDTO> saveTask(@RequestBody @Valid TaskDTO dto) {
        TaskDTO task = service.saveTask(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable @NotNull Long id, @RequestBody @Valid TaskDTO dto) {
        TaskDTO updatedTask = service.updateTask(dto, id);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskDTO> remover(@PathVariable @NotNull Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
