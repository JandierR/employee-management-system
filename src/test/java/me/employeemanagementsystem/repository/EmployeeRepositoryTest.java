package me.employeemanagementsystem.repository;

import me.employeemanagementsystem.entity.Department;
import me.employeemanagementsystem.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;


    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmployeeExists() {
        Department department = new Department(null, "IT", "IT Description");
        Employee employee = new Employee(null, "Jandier", "Rojas", "JanRo@gmail.com", department, "Active", "Developer");

        departmentRepository.save(department);
        employeeRepository.save(employee);

        Boolean exists = employeeRepository.existsByEmail("JanRo@gmail.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmployeeDoesNotExist() {
        Boolean exists = employeeRepository.existsByEmail("JanRo@gmail.com");

        assertThat(exists).isFalse();
    }

    @Test
    void existById_ShouldReturnTrue_WhenEmployeeExists() {
        Department department = new Department(null, "IT", "IT Description");
        Employee employee = new Employee(null, "Jandier", "Rojas", "JanRo@gmail.com", department, "Active", "Developer");

        departmentRepository.save(department);
        employeeRepository.save(employee);

        Boolean exists = employeeRepository.existsById(1000L);

        assertThat(exists).isTrue();
    }

    @Test
    void existById_ShouldReturnFalse_WhenEmployeeDoesNotExist() {

        Boolean exists = employeeRepository.existsById(1000L);

        assertThat(exists).isFalse();
    }

    @Test
    void findByDepartmentId_ShouldReturnEmployeeList_WhenEmployeesExist() {
        Department department = new Department(null, "IT", "IT Description");
        Employee employee1 = new Employee(null, "Jandier", "Rojas", "JanRo@gmail.com", department, "Active", "Developer");
        Employee employee2 = new Employee(null, "Leonardo", "Da Vinci", "leoDa@gmail.com", department, "Inactive", "Painter");

        departmentRepository.save(department);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        List<Employee> employeeList = employeeRepository.findByDepartmentId(department.getId());

        assertThat(employeeList).hasSize(2);

        assertThat(employeeList.get(0).getFirstName()).isEqualTo("Jandier");
        assertThat(employeeList.get(0).getLastName()).isEqualTo("Rojas");
        assertThat(employeeList.get(0).getEmail()).isEqualTo("JanRo@gmail.com");
        assertThat(employeeList.get(0).getDepartment()).isEqualTo(department);
        assertThat(employeeList.get(0).getStatus()).isEqualTo("Active");
        assertThat(employeeList.get(0).getPosition()).isEqualTo("Developer");
    }

    @Test
    void findByDepartmentId_ShouldReturnEmptyList_WhenEmployeesDoNotExist() {

        Department department = new Department(null, "IT", "IT Description");
        Employee employee1 = new Employee(null, "Jandier", "Rojas", "JanRo@gmail.com", department, "Active", "Developer");
        Employee employee2 = new Employee(null, "Leonardo", "Da Vinci", "leoDa@gmail.com", department, "Inactive", "Painter");

        departmentRepository.save(department);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        Long nonExistentDepartmentId = 1001L;

        List<Employee> employeeList = employeeRepository.findByDepartmentId(nonExistentDepartmentId);

        assertThat(employeeList).isEmpty();
    }
}