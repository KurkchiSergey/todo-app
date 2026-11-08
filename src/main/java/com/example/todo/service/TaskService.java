package com.example.todo.service;

import com.example.todo.exception.TaskNotFoundException;
import com.example.todo.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Task findById(Long id) {
        Task task = storage.get(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    public Task create(Task task) {
        long id = sequence.incrementAndGet();
        task.setId(id);
        storage.put(id, task);
        return task;
    }

    public Task update(Long id, Task updated) {
        Task existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setCompleted(updated.isCompleted());
        return existing;
    }

    public void delete(Long id) {
        if (storage.remove(id) == null) {
            throw new TaskNotFoundException(id);
        }
    }
}
