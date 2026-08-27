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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MemoryController {

    @Autowired
    private MemoryRepository repo;

    private final String uploadDir = "uploads/";

    @GetMapping("/")
    public String timeline(@RequestParam(value = "q", required = false) String q,
                           @RequestParam(value = "tag", required = false) String tag,
                           Model model) {

        List<Memory> results;
        if (q != null && !q.isBlank()) {
            results = repo.findByTitleContainingIgnoreCaseOrTextContainingIgnoreCaseOrderByCreatedAtDesc(q, q);
        } else if (tag != null && !tag.isBlank()) {
            results = repo.findByTagsContainingIgnoreCaseOrderByCreatedAtDesc(tag);
        } else {
            results = repo.findAllByOrderByCreatedAtDesc();
        }

        List<Memory> favorites = results.stream().filter(Memory::isFavorite).collect(Collectors.toList());
        List<Memory> rest = results.stream().filter(m -> !m.isFavorite()).collect(Collectors.toList());

        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
        Map<String, List<Memory>> grouped = new LinkedHashMap<>();
        for (Memory m : rest) {
            String key = m.getCreatedAt().format(monthFmt);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
        }

        // Collect all distinct tags across every memory, for the filter pill bar
        Set<String> allTags = new TreeSet<>();
        for (Memory m : repo.findAll()) {
            if (m.getTags() != null && !m.getTags().isBlank()) {
                for (String t : m.getTags().split(",")) {
                    String trimmed = t.trim();
                    if (!trimmed.isEmpty()) allTags.add(trimmed);
                }
            }
        }

        model.addAttribute("favorites", favorites);
        model.addAttribute("grouped", grouped);
        model.addAttribute("allTags", allTags);
        model.addAttribute("query", q);
        model.addAttribute("activeTag", tag);
        model.addAttribute("isEmpty", results.isEmpty());
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
            int max = Math.min(photos.size(), 6);
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

    @PostMapping("/favorite/{id}")
    public String toggleFavorite(@PathVariable Long id) {
        repo.findById(id).ifPresent(m -> {
            m.setFavorite(!m.isFavorite());
            repo.save(m);
        });
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Memory memory = repo.findById(id).orElseThrow();
        model.addAttribute("memory", memory);
        model.addAttribute("isEdit", true);
        return "new-memory";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Memory formMemory,
                         @RequestParam(value = "photos", required = false) List<MultipartFile> photos) throws IOException {
        Memory existing = repo.findById(id).orElseThrow();
        existing.setTitle(formMemory.getTitle());
        existing.setText(formMemory.getText());
        existing.setSongLink(formMemory.getSongLink());
        existing.setTags(formMemory.getTags());

        if (photos != null) {
            Files.createDirectories(Paths.get(uploadDir));
            int room = 6 - existing.getPhotoPaths().size();
            int max = Math.min(photos.size(), Math.max(room, 0));
            for (int i = 0; i < max; i++) {
                MultipartFile photo = photos.get(i);
                if (!photo.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                    photo.transferTo(Paths.get(uploadDir + filename));
                    existing.getPhotoPaths().add("/uploads/" + filename);
                }
            }
        }
        repo.save(existing);
        return "redirect:/";
    }
}