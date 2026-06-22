package com.inventory.management.feature.settings.repository;

import com.inventory.management.feature.settings.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
}