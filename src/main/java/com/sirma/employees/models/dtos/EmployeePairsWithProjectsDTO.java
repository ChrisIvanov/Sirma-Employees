package com.sirma.employees.models.dtos;

import java.util.ArrayList;

public record EmployeePairsWithProjectsDTO(EmployeePairDTO employeePairDTO, ArrayList<CommonProjectDTO> commonProjects) {

    public EmployeePairDTO getEmployeePair() {
        return employeePairDTO;
    }

    public ArrayList<CommonProjectDTO> getCommonProjects() {
        return commonProjects;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(employeePairDTO.employeeOneId()).append(", ")
                .append(employeePairDTO.employeeTwoId()).append(", ")
                .append(employeePairDTO.totalMonths()).append("<br>");

        for (CommonProjectDTO projectDTO : commonProjects) {
            stringBuilder.append(projectDTO.projectId()).append(", ")
                    .append(projectDTO.totalMonths()).append("<br>");
        }

        return stringBuilder.toString();
    }
}
