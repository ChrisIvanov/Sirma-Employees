package com.sirma.employees.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Project() {
    }

    public Long getProjectId() {
        return id;
    }
}
