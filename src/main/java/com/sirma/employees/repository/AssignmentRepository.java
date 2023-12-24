package com.sirma.employees.repository;

import com.sirma.employees.models.EmployeeProject;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;

@Repository
public interface AssignmentRepository extends JpaRepository<EmployeeProject, Long> {
    @Query("INSERT INTO EmployeeProjects (employeeId, projectId, dateFrom, dateTo) VALUES (:employeeId, :projectId, :dateFrom, :dateTo)")
    int addEmployeeToProject(@Param("employeeId") Long employeeId,
                              @Param("projectId") Long projectId,
                              @Param("dateFrom") Date dateFrom,
                              @Param("dateTo") Date dateTo);

    @Query("SELECT ep FROM EmployeeProjects ep WHERE ep.id = :id")
    EmployeeProject getEmployeeProjectById(@Param("id") Long employeeProjectId);

    @Query("SELECT ep FROM EmployeeProjects ep WHERE ep.employeeId = :id")
    ArrayList<EmployeeProject> getAllProjectsByEmployeeId(@Param("id") Long employeeId);

    @Query("SELECT ep FROM EmployeeProjects ep WHERE ep.id = :id")
    ArrayList<EmployeeProject> getAllEmployeesByProjectId(@Param("id") Long projectId);

    @Modifying
    @Query("DELETE FROM EmployeeProjects WHERE id = :id")
    int delete(@Param("id") Long id);
}
