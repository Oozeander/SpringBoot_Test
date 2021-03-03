package com.oozeander.repository;

import com.oozeander.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT emp FROM Employee emp WHERE emp.email = :email")
    Optional<Employee> findByEmail(@Param("email") String email);
}