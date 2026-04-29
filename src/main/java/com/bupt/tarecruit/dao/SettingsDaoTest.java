package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.SettingsDaoImpl;
import com.bupt.tarecruit.model.SystemSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SettingsDaoTest {
    @Test
    void missingSettingsFileReturnsDefaults() throws Exception {
        Path directory = Files.createTempDirectory("settings-dao");
        Path file = directory.resolve("settings.json");
        SettingsDao dao = new SettingsDaoImpl(file);

        SystemSettings settings = dao.getSettings();

        assertEquals(SystemSettings.DEFAULT_APPLICANT_APPLICATION_LIMIT,
                settings.getDefaultApplicantApplicationLimit());
        assertNotNull(settings.getUpdatedAt());
    }

    @Test
    void saveAndReloadSettings() throws Exception {
        Path file = Files.createTempFile("settings", ".json");
        SettingsDao dao = new SettingsDaoImpl(file);
        SystemSettings settings = SystemSettings.defaults();
        settings.setDefaultApplicantApplicationLimit(6);
        dao.save(settings);

        assertEquals(6, dao.getSettings().getDefaultApplicantApplicationLimit());
    }

    @Test
    void legacySettingsWithoutLimitUseDefaultValue() throws Exception {
        Path file = Files.createTempFile("settings-legacy", ".json");
        Files.writeString(file, "{}");
        SettingsDao dao = new SettingsDaoImpl(file);

        assertEquals(SystemSettings.DEFAULT_APPLICANT_APPLICATION_LIMIT,
                dao.getSettings().getDefaultApplicantApplicationLimit());
    }
}
