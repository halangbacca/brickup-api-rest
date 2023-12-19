package app.brickup.apirest.service;

import app.brickup.apirest.dto.TaskDTO;
import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Task;
import app.brickup.apirest.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository repository;

    @Spy
    private ModelMapper mapper = new ModelMapper();

    @InjectMocks
    private TaskService service;

    @Test
    @DisplayName("When there are task records, a list of values must be returned")
    void findAllTasks() {
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", Status.PENDENTE, "www.google.com"),
                new Task(2L, "Task 2", Status.PENDENTE, "www.google.com")
        );
        Mockito.when(repository.findAll()).thenReturn(tasks);

        List<TaskDTO> list = service.findAll();

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(tasks.size(), list.stream().toList().size());
    }

    @Test
    @DisplayName("When there are no task records, it should return an empty list")
    void findAllTasks_Empty() {
        List<TaskDTO> list = service.findAll();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("When there is a task with the given id, the task must be returned")
    void findTask_IdFound() {
        Task task = new Task(1L, "Task 1", Status.PENDENTE, "www.google.com");

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        TaskDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
    }

    @Test
    @DisplayName("When there is no task with the specified id, an exception must be thrown")
    void findTask_IdNotFound() {
        Exception errorMessage = assertThrows(EntityNotFoundException.class,
                () -> service.findById(1L));

        assertEquals("Task not found!", errorMessage.getMessage());
    }

    @Test
    @DisplayName("Must return the saved task")
    void saveTask() {
        Task task = new Task(1L, "Task 1", Status.PENDENTE, "www.google.com");
        MultipartFile multipartFile = null;

        Mockito.when(repository.save(Mockito.any(Task.class)))
                .thenReturn(task);

        var map = mapper.map(task, TaskDTO.class);

        TaskDTO result = service.saveTask(map, multipartFile);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
    }

    @Test
    @DisplayName("Must return the updated task")
    void updateTask() {
        Task task = new Task(1L, "Task 1", Status.PENDENTE, "www.google.com");
        MultipartFile multipartFile = new MockMultipartFile("Mock", "12345".getBytes());

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        Mockito.when(repository.save(Mockito.any(Task.class)))
                .thenReturn(task);

        var map = mapper.map(task, TaskDTO.class);

        TaskDTO result = service.updateTask(map, 1L, multipartFile);

        assertDoesNotThrow(() -> service.updateTask(map, 1L, multipartFile));
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw exception when trying to update task not found")
    void updateTask_IdNotFound() {
        MultipartFile multipartFile = null;
        assertThrows(EntityNotFoundException.class, () -> service.updateTask(mapper.map(new Task(), TaskDTO.class), 1L, multipartFile));
    }

    @Test
    @DisplayName("Must delete a task")
    void deleteTask() {
        Task task = new Task(1L, "Task 1", Status.PENDENTE, "www.google.com");

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(task));

        service.deleteById(task.getId());

        Mockito.verify(repository).findById(task.getId());
        Mockito.verify(repository).deleteById(task.getId());
    }

    @Test
    @DisplayName("Should throw exception when trying to delete task not found")
    void deleteTask_IdNotFound() {
        Exception errorMessage = assertThrows(EntityNotFoundException.class,
                () -> service.deleteById(1L));

        assertEquals("Task not found!", errorMessage.getMessage());
    }

}