package com.jonhvtr.todolist.controller;

import com.jonhvtr.todolist.domain.dto.*;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import com.jonhvtr.todolist.service.TaskService;
import com.jonhvtr.todolist.utils.TaskUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    @MockitoBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    private final UUID VALID_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void getAllTasks_ShouldReturnPagedTasks_WhenPageParametersAreValid() throws Exception {
        when(taskService.getAllTasks(any(TaskFilter.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/tasks")
                        .param("status", "PENDING")
                        .param("priority", "NONE"))
                .andExpect(status().isOk());

        ArgumentCaptor<TaskFilter> filterCaptor =
                ArgumentCaptor.forClass(TaskFilter.class);

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(taskService).getAllTasks(filterCaptor.capture(), pageableCaptor.capture());

        TaskFilter filter = filterCaptor.getValue();
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(Status.PENDING, filter.status());
        assertEquals(Priority.NONE, filter.priority());

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertTrue(pageable.getSort().isSorted());
        assertNotNull(pageable.getSort().getOrderFor("title"));
    }


    @Test
    @WithMockUser
    void getTaskById_ShouldReturn200AndTask_WhenIdIsValid() throws Exception {
        Task taskRequest = TaskUtils.defaultTaskEntityWithId(VALID_ID);
        TaskResponse taskResponse = TaskUtils.defaultTaskResponse(taskRequest);

        when(taskService.getTaskById(VALID_ID))
                .thenReturn(taskResponse);

        mockMvc.perform(get("/tasks/{taskId}", VALID_ID.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID.toString()))
                .andExpect(jsonPath("$.title").value("Task"));
    }

    @Test
    @WithMockUser
    void getTasksByMonth_ShouldReturnTasksMatchingYearAndMonth() throws Exception {
        TaskResponse taskResponse = TaskUtils.defaultTaskResponse(TaskUtils.defaultTaskEntityWithId(VALID_ID));

        when(taskService.getAllByMonth(any(TaskByMonth.class)))
                .thenReturn(Collections.singletonList(taskResponse));

        mockMvc.perform(get("/tasks/calendar")
                        .param("year", "2025")
                        .param("month", "12"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<TaskByMonth> filterCaptor =
                ArgumentCaptor.forClass(TaskByMonth.class);

        verify(taskService).getAllByMonth(filterCaptor.capture());

        TaskByMonth filter = filterCaptor.getValue();

        assertEquals(2025, filter.year());
        assertEquals(12, filter.month());
    }

    @Test
    @WithMockUser
    void searchTasks_ShouldReturnMatchingTasks_WhenTitleExists() throws Exception {
        TaskResponse taskResponse = TaskUtils.defaultTaskResponse(TaskUtils.defaultTaskEntityWithId(VALID_ID));

        when(taskService.searchTask(any(String.class)))
                .thenReturn(Collections.singletonList(taskResponse));

        mockMvc.perform(get("/tasks/search")
                        .param("search", "Task"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Task"));

        verify(taskService).searchTask(any(String.class));
    }

    @Test
    @WithMockUser
    void getReminders_ShouldReturn200_WhenGetAllReminders() throws Exception {
        TaskResponse response = TaskUtils.defaultTaskResponse(TaskUtils.defaultTaskEntityWithId(VALID_ID));

        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.getReminders(eq(ReminderFilter.ALL), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks/reminder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value(response.title()));

        verify(taskService).getReminders(eq(ReminderFilter.ALL), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void getReminders_ShouldReturn200_WhenFilterPending() throws Exception {
        TaskResponse response = TaskUtils.defaultTaskResponse(TaskUtils.defaultTaskEntityWithId(VALID_ID));

        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.getReminders(eq(ReminderFilter.PENDING), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks/reminder").param("filter", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").hasJsonPath());

        verify(taskService).getReminders(eq(ReminderFilter.PENDING), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void getReminders_ShouldReturn400_WhenFilterInvalid() throws Exception {
        mockMvc.perform(get("/tasks/reminder").param("filter", "INVALID"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser
    void createTask_ShouldReturn201AndCreatedTask_WhenDataIsValid() throws Exception {
        TaskResponse taskResponse = TaskUtils.defaultTaskResponse(TaskUtils.defaultTaskEntityWithId(VALID_ID));
        String json = """
                    {
                      "title": "Task",
                      "content": "Content Task",
                      "dueDate": "02/05/2026 18:00",
                      "priority": "HIGH"
                    }
                """;

        when(taskService.createTask(any(TaskRequest.class)))
                .thenReturn(taskResponse);


        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(TaskRequest.class));
    }

    @Test
    @WithMockUser
    void completeTask_ShouldReturnTaskWithStatusCompleted_WhenIdIsValid() throws Exception {
        doNothing().when(taskService).completedTask(any(UUID.class));

        mockMvc.perform(patch("/tasks/{taskId}/complete", VALID_ID.toString())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).completedTask(any(UUID.class));
    }

    @Test
    @WithMockUser
    void addDueDate_ShouldReturnTaskWithDueDate_WhenIdAndDateAreValid() throws Exception {
        LocalDateTime dueDate = LocalDateTime.of(2025, 12, 1, 10, 0);

        String json = """
                {
                "dateTime": "01/12/2025 10:00"
                }
                """;

        doNothing().when(taskService).addDueDate(any(UUID.class), any(AddDate.class));

        mockMvc.perform(patch("/tasks/{taskId}/due-date", VALID_ID.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        ArgumentCaptor<AddDate> addDateCaptor =
                ArgumentCaptor.forClass(AddDate.class);

        verify(taskService).addDueDate(any(UUID.class), addDateCaptor.capture());

        AddDate addDate = addDateCaptor.getValue();

        assertEquals(dueDate, addDate.dateTime());
    }

    @Test
    @WithMockUser
    void addReminder_ShouldReturnTaskWithReminder_WhenIdAndDateAreValid() throws Exception {
        LocalDateTime reminder = LocalDateTime.of(2025, 11, 30, 10, 0);
        String json = """
                {
                "dateTime": "30/11/2025 10:00"
                }
                """;

        doNothing().when(taskService).addReminderToTask(any(UUID.class), any(AddDate.class));

        mockMvc.perform(patch("/tasks/{taskId}/add-reminder", VALID_ID.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        ArgumentCaptor<AddDate> addDateCaptor =
                ArgumentCaptor.forClass(AddDate.class);

        verify(taskService).addReminderToTask(any(UUID.class), addDateCaptor.capture());

        AddDate addDate = addDateCaptor.getValue();

        assertEquals(reminder, addDate.dateTime());
    }

    @Test
    @WithMockUser
    void shouldReturn400_WhenDateFormatIsInvalid() throws Exception {
        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "dueDate": "2025-04-30T10:00:00"
                                    }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request body"));
    }

    @Test
    @WithMockUser
    void removeReminder_ShouldReturnTaskWithoutReminder_WhenIdIsValid() throws Exception {
        doNothing().when(taskService).removeReminderFromTask(any(UUID.class));

        mockMvc.perform(patch("/tasks/{taskId}/remove-reminder", VALID_ID.toString())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).removeReminderFromTask(any(UUID.class));
    }

    @Test
    @WithMockUser
    void updateTask_ShouldUpdateAndReturnTask_WhenDataIsValid() throws Exception {
        String json = """
                {
                "title": "Task 1",
                "content": "Content 1",
                "dueDate": "27/02/2026 14:00",
                "status": "COMPLETED",
                "priority": "LOW",
                "reminderDateTime": "27/02/2026 09:00"
                }
                """;

        doNothing().when(taskService).updateTask(any(UUID.class), any(TaskUpdate.class));

        mockMvc.perform(put("/tasks/{taskId}", VALID_ID.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<TaskUpdate> updateCaptor =
                ArgumentCaptor.forClass(TaskUpdate.class);

        verify(taskService).updateTask(any(UUID.class), updateCaptor.capture());

        TaskUpdate update = updateCaptor.getValue();

        assertEquals("Task 1", update.title());
        assertEquals("Content 1", update.content());
    }

    @Test
    @WithMockUser
    void deleteTask_ShouldReturn204_WhenIdExists() throws Exception {
        doNothing().when(taskService).deleteTask(any(UUID.class));

        mockMvc.perform(delete("/tasks/{taskId}", VALID_ID.toString())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(any(UUID.class));
    }


}
