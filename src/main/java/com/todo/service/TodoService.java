package com.todo.service;


import com.todo.dto.TodoDto;
import com.todo.entity.StatusTodo;
import com.todo.entity.Todo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TodoService {

    TodoDto addTodo(TodoDto todoDto);

    TodoDto getTodo(Long id);

    List<TodoDto> getAllTodos();

    TodoDto updateTodo(TodoDto todoDto, Long id);

    void deleteTodo(Long id);

    TodoDto completeTodo(Long id);

    TodoDto inCompleteTodo(Long id);

    Page<TodoDto> getTodosPagination(Integer pageNumber, Integer pageSize);

    Page<TodoDto> getTodosPaginationWithFilter(Integer pageNumber, Integer pageSize, String status);
}
