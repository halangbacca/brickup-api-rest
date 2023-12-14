package app.brickup.apirest.service;

import app.brickup.apirest.dto.TarefaDTO;
import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Tarefa;
import app.brickup.apirest.repository.TarefaRepository;
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

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {
    @Mock
    private TarefaRepository repository;

    @Spy
    private ModelMapper mapper = new ModelMapper();

    @InjectMocks
    private TarefaService service;

    @Test
    @DisplayName("Quando há registros de tarefas, deve retornar lista com valores")
    void buscaTodasAsTarefas() {
        List<Tarefa> tarefas = List.of(
                new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false),
                new Tarefa(2L, "Tarefa 2", Status.PENDENTE, "www.google.com", false)
        );
        Mockito.when(repository.findAll()).thenReturn(tarefas);

        List<TarefaDTO> lista = service.buscaTodasAsTarefas();

        assertNotNull(lista);
        assertFalse(lista.isEmpty());
        assertEquals(tarefas.size(), lista.stream().toList().size());
    }

    @Test
    @DisplayName("Quando não há registros de tarefas, deve retornar uma lista vazia")
    void buscaTodasAsTarefas_semRegistros() {
        List<TarefaDTO> lista = service.buscaTodasAsTarefas();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("Quando existe tarefa com o id informado, deve retornar a tarefa")
    void buscarTarefaPeloId() {
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(tarefa));

        TarefaDTO resultado = service.buscaPorId(1L);

        assertNotNull(resultado);
        assertEquals(tarefa.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Quando não existe tarefa com o id informado, deve lançar exceção")
    void buscarTarefaNaoEncontrada() {
        Exception errorMessage = assertThrows(EntityNotFoundException.class,
                () -> service.buscaPorId(1L));

        assertEquals("Tarefa não encontrada!", errorMessage.getMessage());
    }

    @Test
    @DisplayName("Deve retornar a tarefa salva")
    void cadastrarTarefa() {
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        Mockito.when(repository.save(Mockito.any(Tarefa.class)))
                .thenReturn(tarefa);

        var map = mapper.map(tarefa, TarefaDTO.class);

        TarefaDTO resultado = service.cadastraTarefa(map);

        assertNotNull(resultado);
        assertEquals(tarefa.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve retornar a tarefa atualizada")
    void atualizarTarefa() {
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(tarefa));

        Mockito.when(repository.save(Mockito.any(Tarefa.class)))
                .thenReturn(tarefa);

        var map = mapper.map(tarefa, TarefaDTO.class);

        TarefaDTO resultado = service.atualizaTarefa(map, 1L);

        assertDoesNotThrow(() -> service.atualizaTarefa(map, 1L));
        assertNotNull(resultado);
        assertEquals(tarefa.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar tarefa não encontrada")
    void atualizarTarefaNaoEncontrada() {
        assertThrows(EntityNotFoundException.class, () -> service.atualizaTarefa(mapper.map(new Tarefa(), TarefaDTO.class), 1L));
    }

    @Test
    @DisplayName("Deve excluir uma tarefa")
    void excluirTarefa() {
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", Status.PENDENTE, "www.google.com", false);

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(tarefa));

        service.deletarPorId(tarefa.getId());

        Mockito.verify(repository).findById(tarefa.getId());
        Mockito.verify(repository).deleteById(tarefa.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir tarefa não encontrada")
    void excluirTarefaNaoEncontrada() {
        Exception errorMessage = assertThrows(EntityNotFoundException.class,
                () -> service.deletarPorId(1L));

        assertEquals("Tarefa não encontrada!", errorMessage.getMessage());
    }

}