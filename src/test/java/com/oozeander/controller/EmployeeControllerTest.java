package com.oozeander.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oozeander.exception.EmployeeNotFoundException;
import com.oozeander.model.Employee;
import com.oozeander.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(EmployeeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeControllerTest {
    private final String apiBaseUrl = "/api/employees";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private EmployeeServiceImpl employeeService;
    private List<Employee> employees;

    @BeforeEach
    void setup() {
        employees = new ArrayList<>(List.of(
                new Employee("Billel", "KETROUCI", "billel.ketrouci@gmail.com"),
                new Employee("El Bakay", "BOURAJOINI", "elbakay@gmail.com"),
                new Employee("Meriem", "KECHEROUD", "meriemkh@gmail.com")
        ));
    }

    @AfterEach
    void teardown() {
        employees.clear();
    }

    @Test
    @Order(1)
    @Tag("GET_ALL")
    @DisplayName("Should retrieve all employees")
    void getEmployees() throws Exception {
        Mockito.when(employeeService.getEmployees()).thenReturn(employees);
        this.mockMvc.perform(get(apiBaseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].firstName").value("Billel"));
        Mockito.verify(employeeService, Mockito.times(1)).getEmployees();
    }

    @Test
    @Order(2)
    @Tag("GET_ONE")
    @DisplayName("Should retrieve an existing employee")
    void getExistantEmployee() throws Exception {
        String email = "billel.ketrouci@gmail.com";
        Mockito.when(employeeService.getEmployeeByEmail(email))
                .thenReturn(Optional.of(employees.get(0)));
        this.mockMvc.perform(get(apiBaseUrl + "/{email}", email).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Billel"));
        Mockito.verify(employeeService, Mockito.atLeastOnce()).getEmployeeByEmail(Mockito.anyString());
    }

    @Test
    @Order(3)
    @Tag("GET_ONE")
    @DisplayName("Should retrieve an inexistant employee")
    void getInexistantEmployee() throws Exception {
        String email = "elbakay.bourajoini@gmail.com";
        Mockito.when(employeeService.getEmployeeByEmail(Mockito.anyString())).thenThrow(EmployeeNotFoundException.class);
        this.mockMvc.perform(get(apiBaseUrl + "/{email}", email))
                .andExpect(status().isNotFound());
        Mockito.verify(employeeService, Mockito.atLeast(1)).getEmployeeByEmail(Mockito.anyString());
    }

    @Test
    @Order(4)
    @Tag("CREATE")
    @DisplayName("Should save an employee")
    void saveEmployee() throws Exception {
        Mockito.when(employeeService.saveEmployee(employees.get(0))).thenReturn(employees.get(0));
        String employeeJson = mapper.writeValueAsString(employees.get(0));
        mockMvc.perform(post(apiBaseUrl).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).content(employeeJson))
                .andExpect(status().isCreated());
        Mockito.verify(employeeService, Mockito.times(1)).saveEmployee(Mockito.any(Employee.class));
    }
}