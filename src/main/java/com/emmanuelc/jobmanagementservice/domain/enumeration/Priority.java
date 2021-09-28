package com.emmanuelc.jobmanagementservice.domain.enumeration;

public enum Priority {
    HIGH, MEDIUM, LOW;

    @Override
    public String toString() {
        return this.name();
    }
}
