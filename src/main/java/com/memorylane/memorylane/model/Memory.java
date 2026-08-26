package com.memorylane.memorylane.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String text;

    @ElementCollection
    @CollectionTable(name = "memory_photos", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "photo_path")
    @OrderColumn(name = "photo_order")
    private List<String> photoPaths = new ArrayList<>();

    private String songLink;    // Spotify/YouTube URL

    private LocalDateTime createdAt = LocalDateTime.now();

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<String> getPhotoPaths() { return photoPaths; }
    public void setPhotoPaths(List<String> photoPaths) { this.photoPaths = photoPaths; }

    public String getSongLink() { return songLink; }
    public void setSongLink(String songLink) { this.songLink = songLink; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}