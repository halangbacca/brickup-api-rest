package app.brickup.apirest.controller;

import app.brickup.apirest.dto.TaskDTO;
import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Task;
import app.brickup.apirest.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class TasksControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    private ModelMapper mapper = new ModelMapper();

    @MockBean
    private TaskService service;

    @Test
    @DisplayName("When there are no tasks registered, an empty list must be returned")
    void findAllTasks_Empty() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    @DisplayName("When there are tasks registered, a list containing the tasks must be returned.")
    void findAllTasks() throws Exception {
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", Status.PENDING, "www.google.com", false),
                new Task(2L, "Task 2", Status.PENDING, "www.google.com", false)
        );

        var tasksDTO = tasks.stream().map(p -> mapper.map(p, TaskDTO.class)).toList();

        Mockito.when(service.findAll()).thenReturn(tasksDTO);

        mockMvc.perform(get("/api/tasks")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("When there are no tasks registered with the specified id, an exception must be thrown")
    void findTask_IdNotFound() throws Exception {
        Mockito.when(service.findById(Mockito.anyLong())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/api/tasks/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When there is a task registered with the given id, the task must be returned")
    void findTask_IdFound() throws Exception {
        Task task = new Task(1L, "Task 1", Status.PENDING, "www.google.com", false);

        var tasksDTO = mapper.map(task, TaskDTO.class);

        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(tasksDTO);
        mockMvc.perform(get("/api/tasks/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(task.getDescription())));
    }

    @Test
    @DisplayName("When registering a task with invalid data, it must return errors")
    void invalidRegistration() throws Exception {
        Task request = new Task();
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/tasks")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When registering a task with the correct data, you must save the task in the database")
    void saveTask() throws Exception {
        Task request = new Task(1L, "Task 1", Status.PENDING, "www.google.com", false);

        var taskDTO = mapper.map(request, TaskDTO.class);

        Mockito.when(service.saveTask(Mockito.any(TaskDTO.class))).thenReturn(taskDTO);
        String requestJson = objectMapper.writeValueAsString(taskDTO);
        mockMvc.perform(post("/api/tasks")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("When trying to delete task with id not found, an exception should be thrown")
    void deleteTask_IdNotFound() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(service).deleteById(Mockito.anyLong());
        mockMvc.perform(delete("/api/tasks/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Must delete task when id is valid")
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When trying to update a task with invalid data, it should return errors")
    void invalidTaskUpdate() throws Exception {
        Task request = new Task();
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/api/tasks/{id}", 1)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When trying to update task with id not found, exception must be thrown")
    void updateTask_IdNotFound() throws Exception {
        Task request = new Task(1L, "Task 1", Status.PENDING, "www.google.com", false);
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.updateTask(Mockito.any(TaskDTO.class), Mockito.anyLong())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(put("/api/tasks/{id}", 2)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When trying to update a task with a valid id, it must return success")
    void updateTasks() throws Exception {
        Task request = new Task(1L, "Task 1", Status.PENDING, "www.google.com", false);

        var taskDTO = mapper.map(request, TaskDTO.class);

        String requestJson = objectMapper.writeValueAsString(taskDTO);
        Mockito.when(service.updateTask(Mockito.any(TaskDTO.class), Mockito.anyLong())).thenReturn(taskDTO);
        mockMvc.perform(put("/api/tasks/{id}", 1)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(notNullValue())));
    }
}