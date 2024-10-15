package com.todo.utils;

import com.google.gson.Gson;
import com.todo.entity.PublicHoliday;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ValidateDate {

    private final static Collection<DayOfWeek> weekends = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private final static Collection<LocalDate> holidays = new HashSet<>();

    public static void addHoliday(LocalDate date) {
        holidays.add(date);
    }

    public static boolean isHoliday(LocalDate date) {
        return (weekends.contains(date.getDayOfWeek()) || holidays.contains(date));
    }

    public static void getHolidayJSON(int year){
        Gson gson = new Gson();
        String json = getJSONResponde(year);

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

    @Cacheable(cacheNames = {"holidayCache"}, key = "#holidayCache")
    public static String getJSONResponde(int year){
          return new RestTemplate().getForObject("https://date.nager.at/api/v3/publicholidays/"+year+"/KZ", String.class);
    }


}
