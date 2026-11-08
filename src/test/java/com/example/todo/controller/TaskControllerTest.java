package com.example.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.todo.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Long createTask(String title, boolean completed) throws Exception {
        Task t = new Task(null, title, completed);
        String json = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(json).get("id").asLong();
    }

    @Test
    void post_createsTask() throws Exception {
        Task t = new Task(null, "Купить молоко", false);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Купить молоко"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void post_validationFailsOnBlankTitle() throws Exception {
        Task t = new Task(null, "   ", false);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.title").exists());
    }

    @Test
    void get_returnsList() throws Exception {
        createTask("Задача A", false);
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void getOne_returnsTask() throws Exception {
        Long id = createTask("Одна задача", false);
        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void put_updatesTask() throws Exception {
        Long id = createTask("Старое название", false);
        Task upd = new Task(null, "Новое название", true);
        mockMvc.perform(put("/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Новое название"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void delete_removesTask() throws Exception {
        Long id = createTask("Удалить меня", false);
        mockMvc.perform(delete("/tasks/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOne_notFoundReturns404() throws Exception {
        mockMvc.perform(get("/tasks/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
