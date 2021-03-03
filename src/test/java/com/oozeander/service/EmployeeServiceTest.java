package com.oozeander.service;

import com.oozeander.model.Employee;
import com.oozeander.repository.EmployeeRepository;
import com.oozeander.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith({MockitoExtension.class, SpringExtension.class}) // @RunWith(MockitoJUnitRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeServiceTest {
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    @Tag("GET_ALL")
    @Order(1)
    @DisplayName("Should get all employees")
    void getEmployees() {
        Employee e1 = new Employee("Billel", "KETROUCI", "billel.ketrouci@gmail.com"),
            e2 = new Employee("El BAKAY", "BOURAJOINI", "elbakay@gmail.com"),
            e3 = new Employee("Meriem", "KECHEROUD", "meriemkh@gmail.com");
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(e1, e2, e3));
        List<Employee> employees = employeeService.getEmployees();
        Assertions.assertAll(
                () -> assertThat(employees).isNotEmpty(),
                () -> assertThat(employees.size()).isEqualTo(3),
                () -> assertThat(employees).containsExactly(e1, e2, e3)
        );
        Mockito.verify(employeeRepository, Mockito.times(1)).findAll();
    }

    @Nested
    @Tag("GET_ONE")
    @DisplayName("Should get one employee")
    class GetSingle {
        @Test
        @Order(1)
        @DisplayName("By id")
        void getEmployeeById() {
            Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Employee("Billel", "KETROUCI", "billel.ketrouci@gmail.com")));
            Employee employee = employeeService.getEmployeeById(1L).get();
            Assertions.assertAll(
                    () -> assertThat(employee).isInstanceOf(Employee.class),
                    () -> assertThat(employee.getFirstName()).isEqualTo("Billel")
            );
            Mockito.verify(employeeRepository, Mockito.times(1)).findById(Mockito.anyLong());
        }

        @ParameterizedTest(name = "{0} exists in DB ?")
        @Order(2)
        @ValueSource(strings = {"billel.ketrouci@gmail.com", "elbakay@gmail.com"})
        @DisplayName("By email")
        void getEmployeeByEmail(String email) {
            Mockito.when(employeeRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new Employee()));
            Employee employee = employeeService.getEmployeeByEmail(email).get();
            Assertions.assertAll(
                    () -> assertThat(employee).isNotNull(),
                    () -> assertThat(employee).isInstanceOf(Employee.class)
            );
            Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findByEmail(Mockito.anyString());
        }
    }

    @Test
    @Order(2)
    @Tag("CREATE")
    @DisplayName("Should save an employee")
    void saveEmployee() {
        Employee employee = new Employee("Billel", "KETROUCI", "billel.ketrouci@gmail.com");
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
        Employee savedEmployee = employeeService.saveEmployee(employee);
        Assertions.assertAll(
                () -> assertThat(savedEmployee).isInstanceOf(Employee.class),
                () -> assertThat(savedEmployee).isEqualTo(employee)
        );
        Mockito.verify(employeeRepository, Mockito.atLeast(1)).save(Mockito.any(Employee.class));
    }

    @Test
    @Order(3)
    @Tag("DELETE")
    @DisplayName("Should delete an employee")
    void deleteEmployee() {
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(new Employee(), new Employee()));
        List<Employee> employees = new ArrayList<>(employeeService.getEmployees());
        int size = employees.size();
        Mockito.doAnswer(invocation -> employees.remove(invocation.getArgument(0))).when(employeeRepository).deleteById(Mockito.anyLong());
        employeeService.deleteEmployee(Mockito.anyLong());
        assertThat(size).isEqualTo(employees.size());
        Mockito.verify(employeeRepository, Mockito.times(1)).findAll();
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).deleteById(Mockito.anyLong());
    }

    @Test
    @Order(4)
    @Tag("UPDATE")
    @DisplayName("Should update an employee")
    void updateEmployee() {
        Employee employeeUpdatedArg = new Employee("Oozeander", "Billel KETROUCI", "billel.ketrouci@gmail.com");
        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Employee("Billel", "KETROUCI", "billel.ketrouci@gmail.com")));
        Mockito.doReturn(employeeUpdatedArg).when(employeeRepository).save(Mockito.any(Employee.class));
        Employee employeeUpdated = employeeService.updateEmployee(1L, employeeUpdatedArg);
        Assertions.assertAll(
                () -> assertThat(employeeUpdated).isNotNull(),
                () -> assertThat(employeeUpdated).isInstanceOf(Employee.class),
                () -> assertThat(employeeUpdated.getFirstName()).isEqualTo("Oozeander")
        );
        Mockito.verify(employeeRepository, Mockito.atLeastOnce()).findById(Mockito.anyLong());
        Mockito.verify(employeeRepository, Mockito.times(1)).save(Mockito.any(Employee.class));
    }
}