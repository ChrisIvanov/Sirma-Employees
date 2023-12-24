package com.sirma.employees.controllers;

import com.sirma.employees.models.EmployeeProject;
import com.sirma.employees.models.dtos.EmployeeProjectDTO;
import com.sirma.employees.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, AssignmentRepository assignmentRepository) {
        this.employeeRepository = employeeRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @PostMapping("/add-employee-project")
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeProjectDTO employeeProjectDTO) {
        try {
            EmployeeProject employeeProject = new EmployeeProject(
                    employeeProjectDTO.employeeId(),
                    employeeProjectDTO.projectId(),
                    employeeProjectDTO.dateFrom(),
                    employeeProjectDTO.dateTo());

            assignmentRepository.save(employeeProject);

            return ResponseEntity.ok("Entity added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding entity");
        }
    }
}
