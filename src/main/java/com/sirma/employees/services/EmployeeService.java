package com.sirma.employees.services;

import com.sirma.employees.models.dtos.*;
import com.sirma.employees.models.*;
import com.sirma.employees.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    @Autowired
    private static EmployeeRepository employeeRepository;

    public void addEmployee(Long id) {
        int result = employeeRepository.addEmployee(id);

        if (result == 1) {
            System.out.println("Employee added successfully");
        } else {
            System.out.println("Employee addition failed");
        }
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.getEmployeeById(id);

        return new EmployeeDTO(employee.getEmployeeId(), employee.getAssignedToProject());
    }

    public void changeAssignedStatus(Long id, boolean isAssigned) {
        EmployeeDTO employee = getEmployeeById(id);

        if (employee.isAssignedToProject() == isAssigned) {
            System.out.println("The employee's assignment status is the same as the one you want to set.");
        } else {
            employeeRepository.changeAssignmentState(id, isAssigned);
        }
    }

    public void deleteEmployee(Long id) {
        int result = employeeRepository.delete(id);

        if (result == 0) {
            System.out.println("Unsuccessful attempt to delete an employee.");
        } else {
            System.out.println("Employee with id " + id + " was deleted successfully.");
        }
    }
}
