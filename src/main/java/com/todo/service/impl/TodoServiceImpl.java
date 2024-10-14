package com.todo.service.impl;

import com.todo.dto.TodoDto;
import com.todo.entity.PublicHoliday;
import com.todo.entity.StatusTodo;
import com.todo.entity.Todo;
import com.todo.exception.ForbiddenDate;
import com.todo.exception.TodoNotFoundException;
import com.todo.repository.TodoRepository;
import com.todo.service.TodoService;
import com.todo.utils.ValidateDate;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


import org.springframework.web.client.RestTemplate;
import com.google.gson.*;

@Service
public class TodoServiceImpl implements TodoService {

    private ModelMapper modelMapper;
    private TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository, ModelMapper modelMapper) {
        this.todoRepository = todoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TodoDto addTodo(TodoDto todoDto) throws ForbiddenDate {

        todoDto.setStatus(StatusTodo.NOT_COMPLETED);
        //Заполним массив праздников;
        LocalDate date = todoDto.getEstimatedOn();
        ValidateDate.getHolidayJSON(date.getYear());

        LocalDate tempDate = date;
        while(ValidateDate.isHoliday(tempDate)) {
            tempDate = tempDate.plusDays(1);
        }

        if (ValidateDate.isHoliday(date)) {
            throw new ForbiddenDate("В праздники и выходные нельзя назначать дату выполнения " + tempDate);
        }
        Todo todo = modelMapper.map(todoDto, Todo.class);
        Todo savedTodo = todoRepository.save(todo);
        TodoDto savedTodoDto = modelMapper.map(savedTodo, TodoDto.class);
        return savedTodoDto;
    }

    @Override
    public TodoDto getTodo(Long id) {

        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("Нет записи с id - " + id));
        return modelMapper.map(todo, TodoDto.class);
        
    }

    @Override
    public List<TodoDto> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        return todos.stream().map((todo)->modelMapper.map(todo, TodoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TodoDto updateTodo(TodoDto todoDto, Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Нет записи с id - " + id));

        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription());
        todo.setStatus(todoDto.getStatus());
        todo.setEstimatedOn(todoDto.getEstimatedOn());

        Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("Нет записи с id - " + id));
        todoRepository.deleteById(todo.getId());
    }

    @Override
    public TodoDto completeTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("Нет записи с id - " + id));
        todo.setStatus(StatusTodo.COMPLETED);

        Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public TodoDto inCompleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("Нет записи с id - " + id));

        todo.setStatus(StatusTodo.NOT_COMPLETED);
        Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public Page<TodoDto> getTodosPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Todo> todos = todoRepository.findAll(pageable);
        return todos.map(todo -> modelMapper.map(todo, TodoDto.class));
    }

    @Override
    public Page<TodoDto> getTodosPaginationWithFilter(Integer pageNumber, Integer pageSize, String status) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Todo> todos;
        if (status != null && (status.equals("not_completed") || status.equals("completed"))) {
            StatusTodo statusTodo = null;
            if (status.equals("completed")) statusTodo = StatusTodo.COMPLETED;
            else {
                statusTodo = StatusTodo.NOT_COMPLETED;
            }
            todos = todoRepository.findTodoByStatus(statusTodo, pageable);
        }
        else {
            todos = todoRepository.findAll(pageable);
        }

        return todos.map(todo -> modelMapper.map(todo, TodoDto.class));
    }

}
