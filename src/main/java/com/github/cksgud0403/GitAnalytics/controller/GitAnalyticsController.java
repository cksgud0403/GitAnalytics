package com.github.cksgud0403.GitAnalytics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class GitAnalyticsController {


    @GetMapping("/")
    public String home(
            @RequestParam(name = "path", defaultValue = ".") String path,
            @RequestParam(name = "limit", defaultValue = "2147483647") int limit,
            @RequestParam(name = "reverse", defaultValue = "true") boolean reverse,
            Model model) {

        List<File> fileList = getFileRecursively(path, limit, reverse);
        List<String> filePaths = fileList.stream()
                .map(File::getPath).toList();
        model.addAttribute("filePaths", filePaths);

        return "index";
    }


    public List<File> getFileRecursively(String path, int limit, boolean reverse) {
        List<File> fileList;

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            fileList = paths.filter(Files::isRegularFile)
                    .filter(p -> !p.toString().endsWith(".jar"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(reverse) {
            fileList.sort((File f1, File f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        }

        return fileList.subList(0, Math.min(fileList.size(), limit));

    }
}
