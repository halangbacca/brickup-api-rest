package app.brickup.apirest.controller;

import app.brickup.apirest.dto.TarefaDTO;
import app.brickup.apirest.service.TarefaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {
    @Autowired
    private TarefaService service;

    @GetMapping
    public List<TarefaDTO> listarTodasAsTarefas() {
        return service.buscaTodasAsTarefas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaDTO> listarPorId(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(service.buscaPorId(id));
    }

    @PostMapping
    public ResponseEntity<TarefaDTO> cadastrar(@RequestBody @Valid TarefaDTO dto) {
        TarefaDTO tarefa = service.cadastraTarefa(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTO> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid TarefaDTO dto) {
        TarefaDTO atualizado = service.atualizaTarefa(dto, id);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TarefaDTO> remover(@PathVariable @NotNull Long id) {
        service.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

}
