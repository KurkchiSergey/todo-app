package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // POST /tasks — добавить задачу
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody Task task) {
        return service.create(task);
    }

    // GET /tasks — получить все задачи
    @GetMapping
    public List<Task> getAll() {
        return service.findAll();
    }

    // GET /tasks/{id} — получить одну задачу
    @GetMapping("/{id}")
    public Task getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    // PUT /tasks/{id} — обновить задачу
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody Task task) {
        return service.update(id, task);
    }

    // DELETE /tasks/{id} — удалить задачу
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
