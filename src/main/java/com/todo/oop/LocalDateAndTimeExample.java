package com.todo.oop;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

public class LocalDateAndTimeExample {

    public static void main(String[] args) {
        System.out.println("fsdfsdf");

        LocalDate ld = LocalDate.of(2014, 2 , 13);
        System.out.println(ld);
        ld = ld.plusDays(5);
        System.out.println(ld);
        ld = ld.minusDays(15);
        System.out.println(ld);

//        LocalDate ld2 = LocalDate.of(2014, Month.APRIL , 13);
//        System.out.println(ld2);
//
//        LocalTime ld3 = LocalTime.of(14, 23, 13);
//        System.out.println(ld3);



    }
}
