package app.brickup.apirest.service;

import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Task;
import app.brickup.apirest.dto.TaskDTO;
import app.brickup.apirest.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final ModelMapper modelMapper;

    public TaskService(TaskRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public List<TaskDTO> findAll() {
        return repository
                .findAll()
                .stream()
                .map(p -> modelMapper.map(p, TaskDTO.class))
                .toList();
    }

    public TaskDTO findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found!"));

        return modelMapper.map(task, TaskDTO.class);
    }

    public TaskDTO saveTask(TaskDTO dto) {
        Task task = modelMapper.map(dto, Task.class);

        task.setStatus(Status.PENDING);
        task.setIsCompleted(false);

        repository.save(task);

        return modelMapper.map(task, TaskDTO.class);
    }

    public TaskDTO updateTask(TaskDTO dto, Long id) {
        Task tarefaAntiga = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found!"));

        Task updatedTask = modelMapper.map(dto, Task.class);
        updatedTask.setId(id);

        if (dto.getStatus() == null) {
            updatedTask.setStatus(tarefaAntiga.getStatus());
        }

        if (dto.getIsCompleted() == null) {
            updatedTask.setIsCompleted(tarefaAntiga.getIsCompleted());
        }

        updatedTask = repository.save(updatedTask);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    public void deleteById(Long id) {
        repository.findById(id)
                .map(task -> {
                    repository.deleteById(id);
                    return true;
                })
                .orElseThrow(() -> new EntityNotFoundException("Task not found!"));
    }
}
