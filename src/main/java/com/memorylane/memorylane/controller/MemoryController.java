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
import java.util.List;

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
                       @RequestParam(value = "photos", required = false) List<MultipartFile> photos) throws IOException {
        if (photos != null) {
            Files.createDirectories(Paths.get(uploadDir));
            int max = Math.min(photos.size(), 6); // cap at 6, even if more are selected
            for (int i = 0; i < max; i++) {
                MultipartFile photo = photos.get(i);
                if (!photo.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                    photo.transferTo(Paths.get(uploadDir + filename));
                    memory.getPhotoPaths().add("/uploads/" + filename);
                }
            }
        }
        repo.save(memory);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}