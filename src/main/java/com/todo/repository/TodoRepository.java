package com.todo.repository;

import com.todo.entity.StatusTodo;
import com.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findTodoByStatus(StatusTodo status, Pageable pageable);
}
