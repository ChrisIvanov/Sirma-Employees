package com.sirma.employees.models;

import jakarta.persistence.*;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    private boolean isAssignedToProject;

    public Employee(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return id;
    }

    public void setAssignedToProject(boolean isAssignedToProject) {
        this.isAssignedToProject = isAssignedToProject;
    }

    public boolean getAssignedToProject() {
        return isAssignedToProject;
    }
}
