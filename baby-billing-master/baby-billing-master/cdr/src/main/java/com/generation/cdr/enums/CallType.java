package com.generation.cdr.enums;

public enum CallType {
    OUTGOING("01"),
    INCOMING("02");

    private final String code;

    CallType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
