package com.todo;

import com.todo.dto.TodoDto;
import com.todo.entity.StatusTodo;
import com.todo.service.TodoService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;


@EnableCaching
@SpringBootApplication
public class ToDoListApplication {

	//private TodoService todoService;

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(ToDoListApplication.class, args);
	}

	@Bean
	public CommandLineRunner initialCreate(TodoService todoService) {
		return (args) -> {
			TodoDto tdo1 = new TodoDto("Задача 1",
					"Задача по решению вопроса о языке программирования Java",
					StatusTodo.NOT_COMPLETED,
					LocalDate.of(2024, 10, 7),
					LocalDate.of(2024, 10, 9)
			);
			todoService.addTodo(tdo1);

			TodoDto tdo2 = new TodoDto("Task 2",
					"Сходить с магазин и купить молоко и хлеб",
					StatusTodo.NOT_COMPLETED,
					LocalDate.of(2024, 10, 7),
					LocalDate.of(2024, 10, 9)
			);
			todoService.addTodo(tdo2);

			TodoDto tdo3 = new TodoDto("Напоминание 3",
					"Родительское собрание",
					StatusTodo.COMPLETED,
					LocalDate.of(2024, 10, 7),
					LocalDate.of(2024, 10, 9)
			);
			todoService.addTodo(tdo3);

			TodoDto tdo4 = new TodoDto("Задача 001",
					"Тренировка игра на фортепиано",
					StatusTodo.NOT_COMPLETED,
					LocalDate.of(2024, 10, 7),
					LocalDate.of(2024, 10, 9)
			);
			todoService.addTodo(tdo4);
		};
	}

}
