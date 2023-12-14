package app.brickup.apirest.service;

import app.brickup.apirest.model.Status;
import app.brickup.apirest.model.Tarefa;
import app.brickup.apirest.dto.TarefaDTO;
import app.brickup.apirest.repository.TarefaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository repository;
    private final ModelMapper modelMapper;

    public TarefaService(TarefaRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public List<TarefaDTO> buscaTodasAsTarefas() {
        return repository
                .findAll()
                .stream()
                .map(p -> modelMapper.map(p, TarefaDTO.class))
                .toList();
    }

    public TarefaDTO buscaPorId(Long id) {
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada!"));

        return modelMapper.map(tarefa, TarefaDTO.class);
    }

    public TarefaDTO cadastraTarefa(TarefaDTO dto) {
        Tarefa tarefa = modelMapper.map(dto, Tarefa.class);

        tarefa.setStatus(Status.PENDENTE);
        tarefa.setFinalizado(false);

        repository.save(tarefa);

        return modelMapper.map(tarefa, TarefaDTO.class);
    }

    public TarefaDTO atualizaTarefa(TarefaDTO dto, Long id) {
        Tarefa tarefaAntiga = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada!"));

        Tarefa tarefaAtualizada = modelMapper.map(dto, Tarefa.class);
        tarefaAtualizada.setId(id);

        if (dto.getStatus() == null) {
            tarefaAtualizada.setStatus(tarefaAntiga.getStatus());
        }

        if (dto.getFinalizado() == null) {
            tarefaAtualizada.setFinalizado(tarefaAntiga.getFinalizado());
        }

        tarefaAtualizada = repository.save(tarefaAtualizada);
        return modelMapper.map(tarefaAtualizada, TarefaDTO.class);
    }

    public void deletarPorId(Long id) {
        repository.findById(id)
                .map(tarefa -> {
                    repository.deleteById(id);
                    return true;
                })
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada!"));
    }
}
