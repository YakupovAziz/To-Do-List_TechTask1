package com.todo.service.impl;

import com.todo.dto.TodoDto;
import com.todo.entity.PublicHoliday;
import com.todo.entity.StatusTodo;
import com.todo.entity.Todo;
import com.todo.exception.ForbiddenDate;
import com.todo.exception.TodoNotFoundException;
import com.todo.repository.TodoRepository;
import com.todo.service.TodoService;
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


    private static Collection<DayOfWeek> weekends = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static Collection<LocalDate> holidays = new HashSet<>();

    public static void addHoliday(LocalDate date) {
        holidays.add(date);
    }

    public static boolean isHoliday(LocalDate date) {
        return (weekends.contains(date.getDayOfWeek()) || holidays.contains(date));
    }

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
        getHolidayJSON(date.getYear());

        LocalDate tempDate = date;
        while(isHoliday(tempDate)) {
            tempDate = tempDate.plusDays(1);
        }

        if (isHoliday(date)) {
            throw new ForbiddenDate("В праздники и выходные нельзя назначать дату выполнения " + tempDate);
        }
//        Calendar instance = Calendar.getInstance();
//        instance.setTime(date); //устанавливаем дату, с которой будет производить операции
//        instance.add(Calendar.DAY_OF_MONTH, 3);// прибавляем 3 дня к установленной дате
//        Date newDate = instance.getTime(); // получаем измененную дату
//String str = day.getDisplayName(TextStyle.FULL, Locale.getDefault());
        //LocalDate date = LocalDate.now();
        //System.out.println(day.getValue()); // => 2
        //System.out.println(str); // => вторник

        //if (day.getValue() == 6 || day.getValue() == 7) {
//            Calendar calendar = new GregorianCalendar(2024, 9, 30);
//            Date date1 = calendar.getTime();
//            System.out.println(date1);
//            throw new ForbiddenDate("Дата не может выпадать на субботу и воскресенье");
       // }

        Todo todo = modelMapper.map(todoDto, Todo.class);
        Todo savedTodo = todoRepository.save(todo);
        TodoDto savedTodoDto = modelMapper.map(savedTodo, TodoDto.class);
        return savedTodoDto;
    }

    @Override
    public TodoDto getTodo(Long id) {

        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("id - " + id));
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
        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("id - " + id));
        todoRepository.deleteById(todo.getId());
    }

    @Override
    public TodoDto completeTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("id - " + id));
        todo.setStatus(StatusTodo.COMPLETED);

        Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public TodoDto inCompleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(()-> new TodoNotFoundException("id - " + id));

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

    @Cacheable(cacheNames = {"holidayCache"}, key = "#holidayCache")
    public static void getHolidayJSON(int year){
        String json = new RestTemplate().getForObject("https://date.nager.at/api/v3/publicholidays/"+year+"/KZ", String.class);

        Gson gson = new Gson();
        PublicHoliday[] userArray = gson.fromJson(json, PublicHoliday[].class);

        for(PublicHoliday publicHoliday : userArray) {
            //2024-01-02
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                LocalDate date1 = LocalDate.parse(publicHoliday.date, formatter);
                addHoliday(date1);
            } catch (DateTimeParseException e) {
                // DateTimeParseException - Text '2019-nov-12' could not be parsed at index 5
                // Exception handling message/mechanism/logging as per company standard
            }
        }

    }

}
