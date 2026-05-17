package com.jonhvtr.todolist.service;

import com.jonhvtr.todolist.domain.dto.*;
import com.jonhvtr.todolist.domain.entities.Client;
import com.jonhvtr.todolist.domain.entities.Task;
import com.jonhvtr.todolist.domain.enums.Priority;
import com.jonhvtr.todolist.domain.enums.Status;
import com.jonhvtr.todolist.exception.task.TaskNotFoundException;
import com.jonhvtr.todolist.infra.security.SecurityUtils;
import com.jonhvtr.todolist.mapper.*;
import com.jonhvtr.todolist.repository.TaskRepository;
import com.jonhvtr.todolist.utils.TaskUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TaskServiceTest {

    private final UUID INVALID_ID = UUID.randomUUID();
    private final UUID VALID_ID = UUID.randomUUID();

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private TaskCreateMapper createMapper;

    @Mock
    private TaskUpdateMapper updateMapper;

    @Mock
    private TaskStatusMapper statusMapper;

    @Mock
    private TaskDueDateMapper dueDateMapper;

    @Mock
    private TaskReminderMapper reminderMapper;

    @Mock
    private TaskMappers taskMappers;

    @InjectMocks
    private TaskService taskService;

    private final UUID clientId = UUID.randomUUID();

    @Test
    void shouldCreateTask_whenValidTaskRequestProvided() {
        Client client = TaskUtils.defaultClient();
        TaskRequest taskRequest = TaskUtils.defaultTask();
        Task savedTask = TaskUtils.defaultTaskEntityWithClient(client);
        TaskResponse expectedResponse = TaskUtils.defaultTaskResponse(savedTask);

        when(taskMappers.create()).thenReturn(createMapper);
        when(securityUtils.getAuthenticationClient()).thenReturn(client);
        when(createMapper.toTaskEntity(any(TaskRequest.class))).thenReturn(Task.builder().build());
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            ReflectionTestUtils.setField(task, "id", VALID_ID);
            ReflectionTestUtils.setField(task, "title", "First Task");
            return task;
        });
        when(createMapper.toTaskResponse(any(Task.class))).thenReturn(expectedResponse);

        TaskResponse createdTask = taskService.createTask(taskRequest);

        assertThat(createdTask.client().email(), is(equalTo(client.getEmail())));
        assertThat(createdTask.title(), is(equalTo(taskRequest.title())));
        assertThat(createdTask.content(), is(equalTo(taskRequest.content())));

        verify(createMapper).toTaskEntity(any(TaskRequest.class));
        verify(securityUtils).getAuthenticationClient();
        verify(taskRepository).save(any(Task.class));
        verify(createMapper).toTaskResponse(any(Task.class));
    }

    @Test
    void shouldReturnTask_whenIdIsProvided() {
        Task expected = TaskUtils.defaultTaskEntityWithId(VALID_ID);
        TaskResponse taskResponse = TaskUtils.defaultTaskResponse(expected);

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(expected.getId(), clientId)).thenReturn(Optional.of(expected));

        TaskResponse taskId = taskService.getTaskById(VALID_ID);
        assertThat(taskId, is(equalTo(taskResponse)));

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(expected.getId(), clientId);
    }

    @Test
    void shouldThrowTaskFoundException_whenIdIsInvalid() {
        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(INVALID_ID, clientId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(INVALID_ID));

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(INVALID_ID, clientId);
    }

    @Test
    void shouldReturnTask_whenTitleExists() {
        Task task = TaskUtils.defaultTaskEntityWithId(clientId);
        TaskResponse expectResponse = TaskUtils.defaultTaskResponse(task);

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.searchTask(task.getTitle());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(expectResponse.id());
        assertThat(result.getFirst().title()).isEqualTo(expectResponse.title());

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldReturnAllTasks_whenNoFiltersProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilter filter = new TaskFilter(null, null);
        Task expected = TaskUtils.defaultTaskEntityWithId(VALID_ID);
        Page<Task> taskPage = new PageImpl<>(List.of(expected), pageable, 1);

        when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(filter, pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo(expected.getTitle());

        verify(taskRepository).findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable));
    }

    @Test
    void shouldReturnAllTasks_whenFilteringByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilter filter = new TaskFilter(Status.PENDING, null);
        Task expected = TaskUtils.defaultTaskEntityWithId(VALID_ID);
        Page<Task> taskPage = new PageImpl<>(List.of(expected), pageable, 1);

        when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(filter, pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().status()).isEqualTo(Status.PENDING);

        verify(taskRepository).findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable));
    }

    @Test
    void shouldReturnAllTasks_whenFilteringByPriority() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilter filter = new TaskFilter(null, Priority.NONE);
        Task expected = TaskUtils.defaultTaskEntityWithId(VALID_ID);
        Page<Task> taskPage = new PageImpl<>(List.of(expected), pageable, 1);

        when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(filter, pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().priority()).isEqualTo(Priority.NONE);

        verify(taskRepository).findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable));
    }

    @Test
    void shouldReturnAllTasks_whenFilteringByStatusAndPriority() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilter filter = new TaskFilter(Status.PENDING, Priority.NONE);
        Task expected = TaskUtils.defaultTaskEntityWithId(VALID_ID);
        Page<Task> taskPage = new PageImpl<>(List.of(expected), pageable, 1);

        when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), eq(pageable))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(filter, pageable);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().status()).isEqualTo(Status.PENDING);
        assertThat(result.getContent().getFirst().priority()).isEqualTo(Priority.NONE);
    }

    @Test
    void shouldReturnAllTask_whenFilteringByYearAndMonth() {
        TaskByMonth filter = new TaskByMonth(2025, 12);
        Task expected = TaskUtils.defaultTaskEntityWithId(clientId);

        when(securityUtils.getAuthenticationClientId())
                .thenReturn(clientId);
        when(taskRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(expected));

        List<TaskResponse> result = taskService.getAllByMonth(filter);

        assertThat(result).hasSize(1);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findAll(any(Specification.class));
    }

    @Test
    void givenValidId_whenUpdate_thenItShouldBeUpdated() {
        Task expectedTask = Task.builder().id(clientId).build();
        TaskUpdate taskUpdate = TaskUtils.updateTask();

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(VALID_ID, clientId)).thenReturn(Optional.of(expectedTask));
        when(taskMappers.update()).thenReturn(updateMapper);
        when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

        taskService.updateTask(VALID_ID, taskUpdate);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(VALID_ID, clientId);
        verify(updateMapper).updateTask(expectedTask, taskUpdate);
        verify(taskRepository).save(expectedTask);
    }

    @Test
    void shouldChangeStatusToCompleted_whenUpdatingTaskStatus() {
        Task existingTask = Task.builder().status(Status.PENDING).build();

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(VALID_ID, clientId)).thenReturn(Optional.of(existingTask));
        when(taskMappers.status()).thenReturn(statusMapper);
        doAnswer(i -> {
            Task task = i.getArgument(1);
            ReflectionTestUtils.setField(task, "status", Status.COMPLETED);
            return null;
        }).when(statusMapper).toCompleted(any(Task.class), any(Task.class));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        taskService.completedTask(VALID_ID);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(VALID_ID, clientId);
        verify(statusMapper).toCompleted(existingTask, existingTask);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void shouldFailToChangeStatusToCompleted_whenTaskIsNotFound() {
        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(INVALID_ID, clientId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.completedTask(INVALID_ID));

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(INVALID_ID, clientId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldAddingDueDate_whenUpdatingTaskDueDate() {
        Task existingTask = Task.builder().dueDate(null).build();
        AddDate dueDate = new AddDate(LocalDateTime.of(2025, 11, 30, 10, 0));

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(VALID_ID, clientId)).thenReturn(Optional.of(existingTask));
        when(taskMappers.dueDate()).thenReturn(dueDateMapper);
        doAnswer(i -> {
            AddDate date = i.getArgument(0);
            Task task = i.getArgument(1);
            ReflectionTestUtils.setField(task, "dueDate", date.dateTime());
            return null;
        }).when(dueDateMapper).applyDueDate(any(AddDate.class), any(Task.class));
        when(taskRepository.save(existingTask)).thenAnswer(i -> i.getArgument(0));

        taskService.addDueDate(VALID_ID, dueDate);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(VALID_ID, clientId);
        verify(dueDateMapper).applyDueDate(dueDate, existingTask);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void shouldFailToAddDueDate_whenTaskIsNotFound() {
        AddDate dueDate = new AddDate(LocalDateTime.of(2025, 11, 30, 10, 0));

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(INVALID_ID, clientId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.addDueDate(INVALID_ID, dueDate));

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(INVALID_ID, clientId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTask_whenIdIsValid() {
        Task expectedDeleted = TaskUtils.defaultTaskEntityWithId(VALID_ID);

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(VALID_ID, clientId)).thenReturn(Optional.of(expectedDeleted));

        taskService.deleteTask(VALID_ID);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(VALID_ID, clientId);
        verify(taskRepository).delete(expectedDeleted);
    }

    @Test
    void shouldFailToDeleteTask_whenTaskIsNotFound() {
        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(INVALID_ID, clientId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(INVALID_ID));

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(INVALID_ID, clientId);
        verify(taskRepository, never()).delete((Task) any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldAddReminderDate_whenTaskExists() {
        Task existingTask = TaskUtils.defaultTaskEntityWithOutReminder();
        AddDate reminderDate = new AddDate(LocalDateTime.of(2025, 11, 30, 10, 0));

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(VALID_ID, clientId)).thenReturn(Optional.of(existingTask));
        when(taskMappers.reminder()).thenReturn(reminderMapper);
        doAnswer(i -> {
            AddDate date = i.getArgument(0);
            Task task = i.getArgument(1);
            ReflectionTestUtils.setField(task, "reminderDateTime", date.dateTime());
            return null;
        }).when(reminderMapper).applyReminder(any(AddDate.class), any(Task.class));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        taskService.addReminderToTask(VALID_ID, reminderDate);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(VALID_ID, clientId);
        verify(reminderMapper).applyReminder(reminderDate, existingTask);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void shouldThrowTaskNotFoundException_whenAddingReminderDateWithInvalidId() {
        AddDate reminderDate = new AddDate(LocalDateTime.of(2025, 11, 30, 10, 0));

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(INVALID_ID, clientId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.addReminderToTask(INVALID_ID, reminderDate));

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(INVALID_ID, clientId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldRemoveReminderDate_whenTaskExists() {
        Task existingTask = TaskUtils.defaultTaskEntityWithReminder();

        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(VALID_ID, clientId)).thenReturn(Optional.of(existingTask));
        when(taskMappers.reminder()).thenReturn(reminderMapper);
        doAnswer(i -> {
            Task task = i.getArgument(1);
            ReflectionTestUtils.setField(task, "reminderDateTime", null);
            return null;
        }).when(reminderMapper).clearReminder(any(Task.class), any(Task.class));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        taskService.removeReminderFromTask(VALID_ID);

        verify(securityUtils).getAuthenticationClientId();
        verify(taskRepository).findByIdAndClientId(VALID_ID, clientId);
        verify(reminderMapper).clearReminder(existingTask, existingTask);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void shouldThrowTaskNotFoundException_whenRemoveReminderDateWithInvalidId() {
        when(securityUtils.getAuthenticationClientId()).thenReturn(clientId);
        when(taskRepository.findByIdAndClientId(INVALID_ID, clientId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.removeReminderFromTask(INVALID_ID));

        verify(taskRepository).findByIdAndClientId(INVALID_ID, clientId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllTasksWithReminder_whenFilterIsAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Task task = TaskUtils.defaultTaskEntityWithReminder();
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<TaskResponse> result =
                taskService.getReminders(ReminderFilter.ALL, pageable);

        assertThat(result.getContent())
                .hasSize(1)
                .allMatch(response -> response.reminderDateTime() != null);

        verify(taskRepository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldReturnOnlyPendingTasks_whenFilterIsPending() {
        Pageable pageable = PageRequest.of(0, 10);

        Task task = TaskUtils.defaultTaskEntityWithReminder();
        task.setStatus(Status.PENDING);

        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<TaskResponse> result =
                taskService.getReminders(ReminderFilter.PENDING, pageable);

        assertThat(result.getContent())
                .hasSize(1)
                .allMatch(response ->
                        response.status() == Status.PENDING
                );

        verify(taskRepository)
                .findAll(any(Specification.class), eq(pageable));
    }
}
