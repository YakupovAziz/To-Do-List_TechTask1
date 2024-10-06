package com.todo.oop;

import java.time.LocalDate;
import java.time.Period;

public class PeriodClassEx {

    static void smenaDejurnogo(LocalDate nachalo, LocalDate konec, Period period){
        LocalDate date = nachalo;
        while(date.isBefore(konec)){
            System.out.println("Nastupila data " + date + " smeny dejurnogo");
            date = date.plus(period);
        }
    }

    public static void main(String[] args) {

        LocalDate nach = LocalDate.of(2024, 9, 1);
        LocalDate kon = LocalDate.of(2025, 5, 31);
        //Period period = Period.between(nach, kon);
        Period period = Period.ofMonths(1);
        smenaDejurnogo(nach, kon, period);
    }
}
