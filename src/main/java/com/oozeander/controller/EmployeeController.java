package com.oozeander.controller;

import com.oozeander.exception.EmployeeNotFoundException;
import com.oozeander.model.Employee;
import com.oozeander.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping("/{email:.+}")
    public Employee getEmployee(@PathVariable("email") String email) {
        Optional<Employee> employeeOptional = employeeService.getEmployeeByEmail(email);
        if (employeeOptional.isPresent())
            return employeeOptional.get();
        else throw new EmployeeNotFoundException(email);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Employee saveEmployee(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }
}