package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.SettingsDao;
import com.bupt.tarecruit.model.SystemSettings;
import com.bupt.tarecruit.util.FileStorageUtil;
import java.nio.file.Path;

public class SettingsDaoImpl implements SettingsDao {
    private final Path settingsFile;

    public SettingsDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("settings.json"));
    }

    public SettingsDaoImpl(final Path settingsFile) {
        this.settingsFile = settingsFile;
    }

    @Override
    public SystemSettings getSettings() {
        return FileStorageUtil.readObject(settingsFile, SystemSettings.class, SystemSettings::defaults);
    }

    @Override
    public void save(final SystemSettings settings) {
        FileStorageUtil.writeObject(settingsFile, settings);
    }
}
