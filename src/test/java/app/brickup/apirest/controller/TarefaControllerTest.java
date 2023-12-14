package app.brickup.apirest.controller;

import app.brickup.apirest.dto.TarefaDTO;
import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Tarefa;
import app.brickup.apirest.service.TarefaService;
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
class TarefaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    private ModelMapper mapper = new ModelMapper();

    @MockBean
    private TarefaService service;

    @Test
    @DisplayName("Quando não há tarefas cadastradas, deve retornar uma lista vazia")
    void buscaTarefasSemConteudo() throws Exception {
        mockMvc.perform(get("/api/tarefas")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    @DisplayName("Quando há tarefas cadastradas, deve retornar uma lista contendo as tarefas")
    void buscaTodasAsTarefas() throws Exception {
        List<Tarefa> tarefas = List.of(
                new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false),
                new Tarefa(2L, "Tarefa 2", Status.PENDENTE, "www.google.com", false)
        );

        var tarefasDTO = tarefas.stream().map(p -> mapper.map(p, TarefaDTO.class)).toList();

        Mockito.when(service.buscaTodasAsTarefas()).thenReturn(tarefasDTO);

        mockMvc.perform(get("/api/tarefas")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Quando não há tarefas cadastradas com o id informado, deve lançar uma exceção")
    void buscaTarefaIdNaoEncontrado() throws Exception {
        Mockito.when(service.buscaPorId(Mockito.anyLong())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/api/tarefas/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Quando há uma tarefa cadastrada com o id informado, deve retornar a tarefa")
    void buscaTarefaPeloId() throws Exception {
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        var tarefaDTO = mapper.map(tarefa, TarefaDTO.class);

        Mockito.when(service.buscaPorId(Mockito.anyLong())).thenReturn(tarefaDTO);
        mockMvc.perform(get("/api/tarefas/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.descricao", is(tarefa.getDescricao())));
    }

    @Test
    @DisplayName("Quando cadastrar uma tarefa com dados inválidos, deve retornar erros")
    void cadastroInvalido() throws Exception {
        Tarefa request = new Tarefa();
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/tarefas")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando cadastrar uma tarefa com os dados corretos, deve salvar a tarefa na base de dados")
    void cadastrarTarefa() throws Exception {
        Tarefa request = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        var tarefaDTO = mapper.map(request, TarefaDTO.class);

        Mockito.when(service.cadastraTarefa(Mockito.any(TarefaDTO.class))).thenReturn(tarefaDTO);
        String requestJson = objectMapper.writeValueAsString(tarefaDTO);
        mockMvc.perform(post("/api/tarefas")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Quando tentar excluir tarefa com id não encontrado, deve lançar exceção")
    void excluirTarefaNaoEncontrada() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(service).deletarPorId(Mockito.anyLong());
        mockMvc.perform(delete("/api/tarefas/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve excluir a tarefa quando o id for válido")
    void excluirTarefa() throws Exception {
        mockMvc.perform(delete("/api/tarefas/{id}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Quando tentar atualizar uma tarefa com dados inválidos, deve retornar erros")
    void AtualizacaoInvalida() throws Exception {
        Tarefa request = new Tarefa();
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/api/tarefas/{id}", 1)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando tentar atualizar tarefa com id não encontrado, deve lançar exceção")
    void atualizarTarefaIdNaoEncontrado() throws Exception {
        Tarefa request = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.atualizaTarefa(Mockito.any(TarefaDTO.class), Mockito.anyLong())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(put("/api/tarefas/{id}", 2)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Quando tentar atualizar uma tarefa com id válido, deve retornar sucesso")
    void atualizarTarefa() throws Exception {
        Tarefa request = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        var tarefaDTO = mapper.map(request, TarefaDTO.class);

        String requestJson = objectMapper.writeValueAsString(tarefaDTO);
        Mockito.when(service.atualizaTarefa(Mockito.any(TarefaDTO.class), Mockito.anyLong())).thenReturn(tarefaDTO);
        mockMvc.perform(put("/api/tarefas/{id}", 1)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.descricao", is(notNullValue())));
    }
}