package com.sirma.employees.models.dtos;

public record CommonProjectDTO(Long projectId, Long totalMonths) {

    @Override
    public String toString() {
        return String.format(projectId + ", " + totalMonths);
    }
}
