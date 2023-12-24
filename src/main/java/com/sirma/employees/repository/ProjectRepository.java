package com.sirma.employees.repository;

import com.sirma.employees.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("INSERT INTO Projects (identityKey) VALUES (:id)")
    int addProject(@Param("id") Long id);

    @Query("SELECT p FROM Projects p WHERE p.id = :id")
    Project getProjectById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Projects WHERE id = :id")
    int delete(@Param("id") Long id);
}
