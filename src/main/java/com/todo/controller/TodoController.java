package com.todo.controller;
import com.todo.dto.TodoDto;
import com.todo.service.TodoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@AllArgsConstructor
public class TodoController {

    private TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoDto> addTodo(@RequestBody TodoDto todoDto){
        TodoDto savedTodo = todoService.addTodo(todoDto);
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable("id") Long todoId){
        TodoDto todoDto = todoService.getTodo(todoId);
        return new ResponseEntity<>(todoDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos(){
        List<TodoDto> todos = todoService.getAllTodos();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todos);
    }

    @GetMapping(value = {"/pagind/{pageNumber}/{pageSize}/{status}", "/pagind/{pageNumber}/{pageSize}"})
    public ResponseEntity<Page<TodoDto>> getTodosPaginationWithFilter(@PathVariable Integer pageNumber,
                                                                      @PathVariable Integer pageSize,
                                                                      @PathVariable(required = false) String status){

        pageNumber = pageNumber != null ? pageNumber : Integer.valueOf(0);
        pageSize = pageSize != null ? pageSize : Integer.valueOf(5);

        Page<TodoDto> todos;

        if (status != null && !status.isEmpty() && (status.equals("completed") || status.equals("not_completed"))) {
            todos = todoService.getTodosPaginationWithFilter(pageNumber, pageSize, status);
        } else {
            todos = todoService.getTodosPagination(pageNumber, pageSize);
        }
        return ResponseEntity.ok(todos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@RequestBody TodoDto todoDto, @PathVariable Long id){
        TodoDto updateTodo = todoService.updateTodo(todoDto, id);
        return ResponseEntity.ok(updateTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable("id") Long todoId){
        todoService.deleteTodo(todoId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }

    @PatchMapping("/complete/{id}")
    public ResponseEntity<TodoDto> completeTodo(@PathVariable Long id){
        TodoDto updatedTodo = todoService.completeTodo(id);
        return ResponseEntity.ok(updatedTodo);
    }

    @PatchMapping("/incomplete/{id}")
    public ResponseEntity<TodoDto> inCompleteTodo(@PathVariable Long id){
        TodoDto updatedTodo = todoService.inCompleteTodo(id);
        return ResponseEntity.ok(updatedTodo);
    }
}
