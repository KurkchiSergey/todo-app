package com.example.todo.service;

import com.example.todo.exception.TaskNotFoundException;
import com.example.todo.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
    }

    @Test
    void create_assignsIdAndStores() {
        Task created = service.create(new Task(null, "Купить молоко", false));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Купить молоко");
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void findAll_returnsAllCreated() {
        service.create(new Task(null, "Задача 1", false));
        service.create(new Task(null, "Задача 2", true));

        List<Task> all = service.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void findById_returnsTask() {
        Task created = service.create(new Task(null, "Задача", false));

        Task found = service.findById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void findById_throwsWhenMissing() {
        assertThrows(TaskNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void update_changesTitleAndCompleted() {
        Task created = service.create(new Task(null, "Старое", false));

        Task updated = service.update(created.getId(), new Task(null, "Новое", true));

        assertThat(updated.getTitle()).isEqualTo("Новое");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void update_throwsWhenMissing() {
        assertThrows(TaskNotFoundException.class,
                () -> service.update(999L, new Task(null, "X", false)));
    }

    @Test
    void delete_removesTask() {
        Task created = service.create(new Task(null, "Удалить", false));

        service.delete(created.getId());

        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void delete_throwsWhenMissing() {
        assertThrows(TaskNotFoundException.class, () -> service.delete(999L));
    }
}
