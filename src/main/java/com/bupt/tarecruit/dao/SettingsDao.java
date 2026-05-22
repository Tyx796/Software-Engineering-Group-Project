package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.SystemSettings;

public interface SettingsDao {
    SystemSettings getSettings();
    void save(SystemSettings settings);
}
