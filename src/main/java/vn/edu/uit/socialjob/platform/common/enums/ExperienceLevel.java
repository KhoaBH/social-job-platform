package vn.edu.uit.socialjob.platform.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExperienceLevel {
    INTERN,
    JUNIOR,
    MIDDLE,
    SENIOR,
    LEAD;

    @JsonCreator
    public static ExperienceLevel forValue(String value) {
        if (value == null) return null;
        switch (value.trim().toUpperCase()) {
            case "INTERN":
                return INTERN;
            case "JUNIOR":
                return JUNIOR;
            case "MID":
            case "MIDDLE":
                return MIDDLE;
            case "SENIOR":
                return SENIOR;
            case "LEAD":
                return LEAD;
            default:
                throw new IllegalArgumentException("Unknown ExperienceLevel: " + value);
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}