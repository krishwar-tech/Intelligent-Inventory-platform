package com.inventory.management.service;

import com.inventory.management.entity.Settings;

public interface SettingsService {

    Settings getSettings();

    Settings save(Settings input);
}