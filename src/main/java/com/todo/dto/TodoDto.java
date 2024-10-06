package com.todo.dto;


import com.todo.entity.StatusTodo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    private Long id;
    private String title;
    private String description;
    private StatusTodo status;
    private LocalDate createdOn;
    private LocalDate estimatedOn;

    public TodoDto(String title, String description, StatusTodo status, LocalDate createdOn, LocalDate estimatedOn) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdOn = createdOn;
        this.estimatedOn = estimatedOn;
    }
}
