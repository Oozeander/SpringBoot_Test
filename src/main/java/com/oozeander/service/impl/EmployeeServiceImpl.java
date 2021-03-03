package com.oozeander.service.impl;

import com.oozeander.model.Employee;
import com.oozeander.repository.EmployeeRepository;
import com.oozeander.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("employeeService")
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl() {}

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired


    @Override
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Optional<Employee> employeeOptional = getEmployeeById(id);
        if (employeeOptional.isPresent()) {
            Employee employeeToUpdate = employeeOptional.get();
            employeeToUpdate.setFirstName(employee.getFirstName());
            employeeToUpdate.setLastName(employee.getLastName());
            employeeToUpdate.setEmail(employee.getEmail());
            employeeRepository.save(employeeToUpdate);
            return employeeToUpdate;
        } return employee;
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}