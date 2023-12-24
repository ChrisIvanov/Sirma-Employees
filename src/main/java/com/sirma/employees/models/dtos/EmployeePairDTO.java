package com.sirma.employees.models.dtos;

public record EmployeePairDTO(Long employeeOneId, Long employeeTwoId, Long totalMonths){

    public Long getTotalMonths() {
        return totalMonths;
    }

    @Override
    public String toString() {
        return String.format(employeeOneId + ", " + employeeTwoId + ", " + totalMonths);
    }
}
