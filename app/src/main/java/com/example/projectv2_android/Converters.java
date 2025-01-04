package com.example.projectv2_android;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {
    @TypeConverter
    public static String fromList(List<Double> grades) {
        if (grades == null) return null;
        return grades.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @TypeConverter
    public static List<Double> toList(String grades) {
        if (grades == null || grades.isEmpty()) return null;
        return Arrays.stream(grades.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
}

