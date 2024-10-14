package com.todo.controller;

import com.todo.entity.StatusTodo;
import com.todo.entity.Todo;
import com.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    private ModelMapper modelMapper;

    @Mock
    TodoService todoService;

    @InjectMocks
    TodoController controller;

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity(){
        //given
        var tasks = List.of(
        new Todo(1L,
                "Title 1",
                "Desc 1",
                StatusTodo.NOT_COMPLETED,
                LocalDate.of(2024, 10, 7),
                LocalDate.of(2024, 10, 9))
        );
        doReturn(tasks).when(this.todoService.getAllTodos().stream().map((todo)->(modelMapper.map(todo, Todo.class))));

        //when
        var responseEntity = this.controller.getAllTodos();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

}