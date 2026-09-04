package com.memorylane.memorylane.model;

import jakarta.persistence.*;

@Entity
public class SiteSettings {

    @Id
    private Long id = 1L; // always a single row

    private String coverPhotoPath;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoverPhotoPath() { return coverPhotoPath; }
    public void setCoverPhotoPath(String coverPhotoPath) { this.coverPhotoPath = coverPhotoPath; }
}