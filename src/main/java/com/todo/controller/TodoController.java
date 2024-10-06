package com.todo.controller;
//-javaagent:C:\jetbraincrack\enc-sniarbtej-2024.2.4.jar
import com.todo.dto.PageRequestDto;
import com.todo.dto.TodoDto;
import com.todo.entity.Todo;
import com.todo.entity.StatusTodo;
import com.todo.exception.ForbiddenDate;
import com.todo.service.TodoService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar ;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

@RestController
//@RequestMapping("/api/todos")
@AllArgsConstructor
public class TodoController {

    private TodoService todoService;

    @PostMapping("/api/todos")
    public ResponseEntity<TodoDto> addTodo(@RequestBody TodoDto todoDto){

        TodoDto savedTodo = todoService.addTodo(todoDto);
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);

//        {
//            "title" : "aziz2",
//             "description": "pass33",
//             "completed":  true
//        }
//        {
//                "title" : "aziz2",
//                "description": "pass33",
//                "createdOn":  "2024-04-13T08:30:00Z",
//                "estimatedOn":  "2024-04-13T08:30:00Z",
//                "status": "в работе"
//        }

    }

    @GetMapping("/api/todos/{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable("id") Long todoId){
        TodoDto todoDto = todoService.getTodo(todoId);
        return new ResponseEntity<>(todoDto, HttpStatus.OK);
    }

    @GetMapping("/api/todos")
    public ResponseEntity<List<TodoDto>> getAllTodos(){
        List<TodoDto> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping(value = {"/api/todos/pagind/{pageNumber}/{pageSize}/{status}", "/api/todos/pagind/{pageNumber}/{pageSize}"})
    public ResponseEntity<Page<TodoDto>> getTodosPaginationWithFilter(@PathVariable Integer pageNumber,
                                                                      @PathVariable Integer pageSize,
                                                                      @PathVariable(required = false) String status){

        pageNumber = pageNumber != null ? pageNumber : Integer.valueOf(0);
        pageSize = pageSize != null ? pageSize : Integer.valueOf(1);

        Page<TodoDto> todos;

        if (status != null && !status.isEmpty() && (status.equals("completed") || status.equals("not_completed"))) {
            todos = todoService.getTodosPaginationWithFilter(pageNumber, pageSize, status);
        } else {
            todos = todoService.getTodosPagination(pageNumber, pageSize);
        }

        return ResponseEntity.ok(todos);
    }

    @PutMapping("/api/todos/{id}")
    public ResponseEntity<TodoDto> updateTodo(@RequestBody TodoDto todoDto, @PathVariable Long id){
        TodoDto updateTodo = todoService.updateTodo(todoDto, id);
        //return new ResponseEntity<>(todoDto, HttpStatus.OK);
        return ResponseEntity.ok(updateTodo);
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable("id") Long todoId){
        todoService.deleteTodo(todoId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/todos/complete/{id}")
    public ResponseEntity<TodoDto> completeTodo(@PathVariable Long id){
        TodoDto updatedTodo = todoService.completeTodo(id);
        return ResponseEntity.ok(updatedTodo);
    }

    @PatchMapping("/api/todos/incomplete/{id}")
    public ResponseEntity<TodoDto> inCompleteTodo(@PathVariable Long id){
        TodoDto updatedTodo = todoService.inCompleteTodo(id);
        return ResponseEntity.ok(updatedTodo);
    }
}
