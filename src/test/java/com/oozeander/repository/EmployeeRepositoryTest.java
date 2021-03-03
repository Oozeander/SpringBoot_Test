package com.oozeander.repository;

import com.oozeander.model.Employee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class) // @RunWith(SpringRunner.class)
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED) // No rollback
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) to user REAL DB
public class EmployeeRepositoryTest {
    private final EmployeeRepository employeeRepository;
    private final TestEntityManager entityManager;

    @Autowired
    public EmployeeRepositoryTest(EmployeeRepository employeeRepository, TestEntityManager entityManager) {
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
    }

    @Test
    @Tag("CREATE")
    @Order(1)
    @DisplayName("Should create an employee")
    void createEmployee() {
        Employee e1 = new Employee("Billel", "KETROUCI", "billel.ketrouci@gmail.com"),
            e2 = new Employee("El Bakay", "BOURAJOINI", "elbakay.bourajoini@gmail.com");
        List<Employee> savedEmployees = employeeRepository.saveAll(List.of(e1, e2));
        e1.setId(1L); e2.setId(2L);
        Assertions.assertAll(
                () -> assertThat(savedEmployees).isNotNull(),
                () -> assertThat(savedEmployees).containsExactly(e1, e2)
        );
    }

    @Test
    @Tag("GET_ALL")
    @Order(2)
    @DisplayName("Should return all employees")
    void getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        Assertions.assertAll(
                () -> assertThat(employees).isNotNull(),
                () -> assertThat(employees.size()).isEqualTo(2)
        );
    }

    // Always executed at the end
    @Nested
    @Tag("GET_ONE")
    @DisplayName("Should get one employee")
    class GetSingle {
        @Test
        @Order(1)
        @DisplayName("By id")
        void getById() {
            Optional<Employee> employeeOptional = employeeRepository.findById(1L);
            Assertions.assertAll(
                    () -> assertThat(employeeOptional).isNotEmpty(),
                    () -> assertThat(employeeOptional.get().getFirstName()).isEqualTo("Oozeander")
            );
        }

        @ParameterizedTest(name = "{0} is in DB ? {1}")
        @Order(2)
        @ValueSource(strings = {"billel.ketrouci@gmail.com", "elbakay@gmail.com"})
        @DisplayName("By email")
        void getByEmail(String email) {
            Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);
            assertThat(email.equals("billel.ketrouci@gmail.com") ? employeeOptional.isPresent() : employeeOptional.isEmpty());
        }
    }

    @Test
    @Tag("UPDATE")
    @Order(3)
    @DisplayName("Should update an employee")
    void updateEmployee() {
        Employee employee = employeeRepository.findById(1L).get();
        employee.setFirstName("Oozeander");
        employee.setLastName("Billel KETROUCI");
        employeeRepository.save(employee);
        Assertions.assertAll(
                () -> assertThat(employeeRepository.findById(1L).get().getFirstName()).isEqualTo("Oozeander"),
                () -> assertThat(employeeRepository.findById(1L).get().getLastName()).isEqualTo("Billel KETROUCI")
        );
    }

    @Test
    @Tag("DELETE")
    @Order(4)
    @DisplayName("Should delete an employee")
    void deleteEmployee() {
        employeeRepository.deleteById(2L);
        List<Employee> employees = employeeRepository.findAll();
        Assertions.assertAll(
                () -> assertThat(employees).isNotEmpty(),
                () -> assertThat(employees.size()).isEqualTo(1),
                () -> assertThat(employees.get(0).getFirstName()).isEqualTo("Oozeander")
        );
    }
}