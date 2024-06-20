package com.consubanco.model.entities.file.util;

import com.consubanco.model.entities.file.File;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class FilterListUtil {

    public static List<File> removeCompoundAttachments(List<File> files) {
        return groupedFiles(files)
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().size() > 1 ? filterGroup(e.getKey(), e.getValue()) : e.getValue().stream())
                .toList();
    }

    private static Map<String, List<File>> groupedFiles(List<File> files) {
        return files.stream()
                .collect(Collectors.groupingBy(File::baseFileName));
    }

    private static Stream<File> filterGroup(String key, List<File> group) {
        return group.stream().filter(file -> !file.getName().equals(key));
    }

}
