package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.SystemSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettingsServiceTest {
    @Test
    void getSettingsReturnsDefaultsWhenFileIsMissing() throws Exception {
        Path directory = Files.createTempDirectory("settings-service");
        Path file = directory.resolve("settings.json");
        SettingsService service = new SettingsService(file);

        assertEquals(SystemSettings.DEFAULT_APPLICANT_APPLICATION_LIMIT,
                service.getSettings().getDefaultApplicantApplicationLimit());
    }

    @Test
    void updateDefaultApplicantApplicationLimitPersistsValue() throws Exception {
        Path file = Files.createTempFile("settings-service", ".json");
        SettingsService service = new SettingsService(file);

        service.updateDefaultApplicantApplicationLimit(7);

        assertEquals(7, new SettingsService(file).getDefaultApplicantApplicationLimit());
    }

    @Test
    void updateDefaultApplicantApplicationLimitRejectsNonPositiveValues() throws Exception {
        Path file = Files.createTempFile("settings-service", ".json");
        SettingsService service = new SettingsService(file);

        assertThrows(IllegalArgumentException.class,
                () -> service.updateDefaultApplicantApplicationLimit(0));
    }
}
