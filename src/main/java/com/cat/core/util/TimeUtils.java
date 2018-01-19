package com.cat.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

@SuppressWarnings({"WeakerAccess", "unused"})
//TODO:时区
public abstract class TimeUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDateTime time) {
        return format(time, DEFAULT_FORMATTER);
    }

    public static String format(LocalDateTime time, DateTimeFormatter formatter) {
        return time.format(formatter);
    }

    public static String format(LocalDate time) {
        return format(time, SIMPLE_FORMATTER);
    }

    public static String format(LocalDate time, DateTimeFormatter formatter) {
        return time.format(formatter);
    }

    public static LocalDateTime parseString(String time, DateTimeFormatter formatter) {
        return LocalDateTime.parse(time, formatter);
    }

    public static LocalDateTime parseString(String time) {
        return LocalDateTime.parse(time, DEFAULT_FORMATTER);
    }

    public static LocalDateTime parseSecond(long seconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault());
    }

    public static LocalDateTime parseMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    //TODO
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(millis(now));
        System.out.println(seconds(now));

        System.out.println(now.atZone(ZoneId.systemDefault()).getLong(ChronoField.INSTANT_SECONDS));
        System.out.println(now.atZone(ZoneId.systemDefault()).getLong(ChronoField.MILLI_OF_DAY));

        System.err.println(parseMillis(0));
    }

    public static long millis(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long seconds(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

}
