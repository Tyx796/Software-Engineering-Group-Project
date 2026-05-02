package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.SettingsDao;
import com.bupt.tarecruit.dao.impl.SettingsDaoImpl;
import com.bupt.tarecruit.model.SystemSettings;
import com.bupt.tarecruit.util.DataValidator;
import java.nio.file.Path;
import java.time.Instant;

public class SettingsService {
    private final SettingsDao settingsDao;

    public SettingsService() {
        this(new SettingsDaoImpl());
    }

    public SettingsService(final Path settingsFile) {
        this(new SettingsDaoImpl(settingsFile));
    }

    public SettingsService(final SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    public SystemSettings getSettings() {
        return settingsDao.getSettings();
    }

    public int getDefaultApplicantApplicationLimit() {
        return getSettings().getDefaultApplicantApplicationLimit();
    }

    public SystemSettings updateDefaultApplicantApplicationLimit(final int limit) {
        DataValidator.validatePositive(limit, "Default applicant application limit");
        SystemSettings settings = getSettings();
        settings.setDefaultApplicantApplicationLimit(limit);
        settings.setUpdatedAt(Instant.now());
        settingsDao.save(settings);
        return settings;
    }
}
