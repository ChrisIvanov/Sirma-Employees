package com.sirma.employees.services;

import com.sirma.employees.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private static ProjectRepository projectRepository;

    public void addProject(Long id) {
        int result = projectRepository.addProject(id);

        if (result == 0) {
            System.out.println("Error! Unsuccessful attempt to create a project.");
        } else {
            System.out.println("Success! Project with id " + id + " was created successfully.");
        }
    }

    public void getProjectById(Long id) {
        var project = projectRepository.getProjectById(id);

        if (project == null) {
            System.out.println("Error! No such project is present in the database");
        } else {
            System.out.println("Success! This project exists.");
        }
    }

    public void deleteProject(Long id) {
        int result = projectRepository.delete(id);

        if (result == 0) {
            System.out.println("Error! Unsuccessful attempt to delete a project.");
        } else {
            System.out.println("Success! Project with id " + id + " was deleted successfully.");
        }
    }
}
