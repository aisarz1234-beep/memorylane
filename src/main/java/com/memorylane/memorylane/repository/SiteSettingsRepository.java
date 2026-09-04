package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {
}