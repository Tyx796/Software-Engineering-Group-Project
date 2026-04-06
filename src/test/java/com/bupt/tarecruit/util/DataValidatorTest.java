package com.bupt.tarecruit.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

class DataValidatorTest {
    @Test
    void validateRequiredRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateRequired("", "Field"));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateRequired(null, "Field"));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateRequired("   ", "Field"));
    }

    @Test
    void validateRequiredAcceptsNonBlank() {
        assertDoesNotThrow(() -> DataValidator.validateRequired("hello", "Field"));
    }

    @Test
    void validateEmailRejectsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateEmail("not-an-email"));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateEmail(""));
    }

    @Test
    void validateEmailAcceptsValid() {
        assertDoesNotThrow(() -> DataValidator.validateEmail("user@example.com"));
    }

    @Test
    void validatePhoneRejectsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validatePhone("abc"));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validatePhone(""));
    }

    @Test
    void validatePhoneAcceptsValid() {
        assertDoesNotThrow(() -> DataValidator.validatePhone("+86 13800000000"));
    }

    @Test
    void validatePasswordRejectsShort() {
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validatePassword("abc"));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validatePassword(""));
    }

    @Test
    void validatePasswordAcceptsSixOrMore() {
        assertDoesNotThrow(() -> DataValidator.validatePassword("secret"));
    }

    @Test
    void validateDeadlineRejectsPast() {
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateDeadline(LocalDate.now().minusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validateDeadline(null));
    }

    @Test
    void validateDeadlineAcceptsTodayOrFuture() {
        assertDoesNotThrow(() -> DataValidator.validateDeadline(LocalDate.now()));
        assertDoesNotThrow(() -> DataValidator.validateDeadline(LocalDate.now().plusDays(7)));
    }

    @Test
    void validatePositiveRejectsZeroAndNegative() {
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validatePositive(0, "Hours"));
        assertThrows(IllegalArgumentException.class, () -> DataValidator.validatePositive(-1, "Hours"));
    }

    @Test
    void validatePositiveAcceptsPositive() {
        assertDoesNotThrow(() -> DataValidator.validatePositive(1, "Hours"));
    }
}
