package com.memorylane.memorylane.controller;

import com.memorylane.memorylane.model.Memory;
import com.memorylane.memorylane.repository.MemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class MemoryController {

    @Autowired
    private MemoryRepository repo;

    private final String uploadDir = "uploads/";

    @GetMapping("/")
    public String timeline(Model model) {
        model.addAttribute("memories", repo.findAllByOrderByCreatedAtDesc());
        return "timeline";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("memory", new Memory());
        return "new-memory";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Memory memory,
                       @RequestParam("photo") MultipartFile photo) throws IOException {
        if (!photo.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));
            String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            photo.transferTo(Paths.get(uploadDir + filename));
            memory.setPhotoPath("/uploads/" + filename);
        }
        repo.save(memory);
        return "redirect:/";
    }
}