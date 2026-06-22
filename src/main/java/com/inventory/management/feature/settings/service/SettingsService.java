package com.inventory.management.feature.settings.service;


import com.inventory.management.feature.settings.entity.Settings;

public interface SettingsService {

    Settings getSettings();

    Settings save(Settings input);
}