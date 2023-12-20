package app.brickup.apirest.service;

import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Task;
import app.brickup.apirest.dto.TaskDTO;
import app.brickup.apirest.repository.TaskRepository;
import app.brickup.apirest.util.UploadImageUtil;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

    public InputStream getResource(Long id) throws FileNotFoundException {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found!"));

        return new FileInputStream(task.getLinkImage());
    }

    public TaskDTO saveTask(TaskDTO dto, MultipartFile image) {
        Task task = modelMapper.map(dto, Task.class);

        try {
            if (UploadImageUtil.uploadImage(image)) {
                System.out.println(UploadImageUtil.imagePath);
                task.setLinkImage(UploadImageUtil.imagePath);
            }
        } catch (Exception e) {
            System.out.println("The image is empty!");
        }

        task.setStatus(Status.PENDENTE);

        repository.save(task);

        return modelMapper.map(task, TaskDTO.class);
    }

    public TaskDTO updateTask(TaskDTO dto, Long id, MultipartFile image) {
        Task oldTask = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found!"));

        Task updatedTask = modelMapper.map(dto, Task.class);
        updatedTask.setId(id);

        if (image != null) {
            try {
                if (UploadImageUtil.uploadImage(image)) {
                    System.out.println(UploadImageUtil.imagePath);
                    updatedTask.setLinkImage(UploadImageUtil.imagePath);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            updatedTask.setLinkImage(oldTask.getLinkImage());
        }

        if (dto.getStatus() == null) {
            updatedTask.setStatus(oldTask.getStatus());
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
