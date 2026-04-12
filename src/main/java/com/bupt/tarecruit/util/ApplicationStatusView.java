package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.ApplicationStatus;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class ApplicationStatusView {
    private ApplicationStatusView() {
    }

    public static Map<String, String> badgeClassByStatusName() {
        return toNameKeyedMap(new EnumMap<>(Map.of(
                ApplicationStatus.PENDING, "app-status-pending",
                ApplicationStatus.REVIEWING, "app-status-reviewing",
                ApplicationStatus.ACCEPTED, "app-status-accepted",
                ApplicationStatus.REJECTED, "app-status-rejected",
                ApplicationStatus.WITHDRAWN, "app-status-withdrawn",
                ApplicationStatus.CANCELLED, "app-status-cancelled")));
    }

    public static Map<String, String> summaryByStatusName() {
        return toNameKeyedMap(new EnumMap<>(Map.of(
                ApplicationStatus.PENDING, "Waiting for organiser review.",
                ApplicationStatus.REVIEWING, "An organiser is reviewing your application.",
                ApplicationStatus.ACCEPTED, "Your application has been accepted.",
                ApplicationStatus.REJECTED, "Your application has been rejected.",
                ApplicationStatus.WITHDRAWN, "You withdrew this application.",
                ApplicationStatus.CANCELLED, "This application was cancelled because the job was cancelled.")));
    }

    public static String badgeClassFor(final ApplicationStatus status) {
        if (status == null) {
            return "app-status-pending";
        }
        return badgeClassByStatusName().getOrDefault(status.name(), "app-status-pending");
    }

    public static String summaryFor(final ApplicationStatus status) {
        if (status == null) {
            return "Application status is currently unavailable.";
        }
        return summaryByStatusName().getOrDefault(status.name(), "Application status is currently unavailable.");
    }

    private static Map<String, String> toNameKeyedMap(final EnumMap<ApplicationStatus, String> values) {
        return values.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(entry -> entry.getKey().name(), Map.Entry::getValue));
    }
}