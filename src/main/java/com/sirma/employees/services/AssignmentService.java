package com.sirma.employees.services;

import com.sirma.employees.models.*;
import com.sirma.employees.models.dtos.*;
import com.sirma.employees.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class AssignmentService {
    @Autowired
    private static AssignmentRepository assignmentRepository;

    public void AssignEmployeeToProject(Long employeeId, Long projectId, Date dateFrom, Date dateTo) {
        int result = assignmentRepository.addEmployeeToProject(employeeId, projectId, dateFrom, dateTo);

        if (result == 0) {
            System.out.println("Error! Employee was not assigned to a project successfully.");
        } else {
            System.out.printf("Success! Employee with id %d was successfully assigned to project %d with dates %tT to %tT", employeeId, projectId, dateFrom, dateTo);
        }
    }

    public EmployeeProjectDTO getEmployeeProjectById(Long id) {
        EmployeeProject employeeProject = assignmentRepository.getEmployeeProjectById(id);

        return new EmployeeProjectDTO(employeeProject.getId(),
                employeeProject.getEmployeeId(),
                employeeProject.getProjectId(),
                employeeProject.getDateFrom(),
                employeeProject.getDateTo());
    }

    public ArrayList<EmployeeProjectDTO> getAllProjectsByEmployeeId(Long id) {
        ArrayList<EmployeeProject> employeeProjectsList = assignmentRepository.getAllProjectsByEmployeeId(id);

        ArrayList<EmployeeProjectDTO> employeeProjectDTOList = new ArrayList<>();

        for (EmployeeProject ep:employeeProjectsList) {
            EmployeeProjectDTO epDTO = new EmployeeProjectDTO(ep.getId(),
                    ep.getEmployeeId(),
                    ep.getProjectId(),
                    ep.getDateFrom(),
                    ep.getDateTo());

            employeeProjectDTOList.add(epDTO);
        }

        return employeeProjectDTOList;
    }

    public ArrayList<EmployeeProjectDTO> getAllEmployeesByProjectId(Long id) {
        ArrayList<EmployeeProject> employeeProjectsList = assignmentRepository.getAllEmployeesByProjectId(id);

        ArrayList<EmployeeProjectDTO> employeeProjectDTOList = new ArrayList<>();

        for (EmployeeProject ep:employeeProjectsList) {
            EmployeeProjectDTO epDTO = new EmployeeProjectDTO(ep.getId(),
                    ep.getEmployeeId(),
                    ep.getProjectId(),
                    ep.getDateFrom(),
                    ep.getDateTo());

            employeeProjectDTOList.add(epDTO);
        }

        return employeeProjectDTOList;
    }

    public void deleteEmployeeProject(Long id) {
        int result = assignmentRepository.delete(id);

        if (result == 0) {
            System.out.println("Error! The employee to project relation was not deleted successfully.");
        } else {
            System.out.println("Success! The employee-project relation was deleted successfully.");
        }
    }
}
