package com.sirma.employees.repository;

import com.sirma.employees.models.Employee;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("INSERT INTO Employees (identityKey) VALUES (:id)")
    int addEmployee(@Param("id") Long id);

    @Query("SELECT e FROM Employees e WHERE e.id = :id")
    Employee getEmployeeById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Employees SET isAssignedToProject = :isAssignedToProject WHERE e.id = :id")
    boolean changeAssignmentState( @Param("id") Long id,
                                   @Param("isAssignedToProject") Boolean isAssignedToProject);

    @Modifying
    @Query("DELETE FROM Employees WHERE id = :id")
    int delete(@Param("id") Long id);

}
