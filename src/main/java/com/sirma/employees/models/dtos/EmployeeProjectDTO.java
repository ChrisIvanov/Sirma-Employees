package com.sirma.employees.models.dtos;

import java.time.LocalDate;

public record EmployeeProjectDTO(Long id, Long employeeId, Long projectId, LocalDate dateFrom, LocalDate dateTo) {

}
