package com.oozeander.service;

import com.oozeander.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Optional<Employee> getEmployeeByEmail(String email);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);
}