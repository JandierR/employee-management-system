package me.employeemanagementsystem.service.impl;

import me.employeemanagementsystem.dto.EmployeeDto;
import me.employeemanagementsystem.entity.Department;
import me.employeemanagementsystem.entity.Employee;
import me.employeemanagementsystem.exception.ResourceNotFoundException;
import me.employeemanagementsystem.repository.DepartmentRepository;
import me.employeemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl employeeServiceImpl;

    @InjectMocks
    private DepartmentServiceImpl departmentServiceImpl;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Test
    void createEmployee() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        Department department = new Department(departmentId, "Engineering", "Engineering department");

        Employee employee = new Employee(employeeId, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department, "Inactive", "IT");

        EmployeeDto employeeDto = new EmployeeDto(null, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department.getId(), "Inactive", "IT");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);

        EmployeeDto result = employeeServiceImpl.createEmployee(employeeDto);

        assertNotNull(result);

        assertEquals("Leonardo", result.getFirstName());
        assertEquals("Da Vinci", result.getLastName());
        assertEquals("LeoVi@gmail.com", result.getEmail());
        assertEquals(department.getId(), result.getDepartmentId());
        assertEquals("Inactive", result.getStatus());
        assertEquals("IT", result.getPosition());

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeRepository, times(1)).existsByEmail(employee.getEmail());
    }

    @Test
    void createEmployeeInvalidDepartment() {
        Long employeeId = 1L;
        Long invalidDepartmentId = 999L;

        Department department = new Department(invalidDepartmentId, "Engineering", "Engineering department");

        Employee employee = new Employee(employeeId, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department, "Inactive", "IT");

        EmployeeDto employeeDto = new EmployeeDto(null, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department.getId(), "Inactive", "IT");

        when(departmentRepository.findById(invalidDepartmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException departmentNotFoundException = assertThrows(ResourceNotFoundException.class,
                () -> employeeServiceImpl.createEmployee(employeeDto));


        assertNotNull(departmentNotFoundException);

        assertEquals("Department not found with ID 999", departmentNotFoundException.getMessage());

        verify(departmentRepository, times(1)).findById(invalidDepartmentId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployeeAlreadyExistError() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        Department department = new Department(departmentId, "Engineering", "Engineering department");

        EmployeeDto employeeDto = new EmployeeDto(employeeId, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department.getId(), "Inactive", "IT");

        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        //I have to enter when we call findById for department, otherwise it will throw the notFoundException if there's no department.
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        IllegalArgumentException employeeExistException = assertThrows(IllegalArgumentException.class,
                () -> employeeServiceImpl.createEmployee(employeeDto));


        assertNotNull(employeeExistException);

        assertEquals("Employee already exist with ID 1", employeeExistException.getMessage());

        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getEmployeeById() {
        Long employeeId = 1L;

        Department department = new Department(1L, "Engineering", "Engineering department");

        Employee employee = new Employee(employeeId, "Jan", "Van Gogh",
                "JanVa@gmail.com", department, "Active", "Developer");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeServiceImpl.getEmployeeById(employeeId);

        assertNotNull(employee);
        assertEquals(employeeId, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Van Gogh", result.getLastName());
        assertEquals("JanVa@gmail.com", result.getEmail());
        assertEquals(department.getId(), result.getDepartmentId());
        assertEquals("Active", result.getStatus());
        assertEquals("Developer", result.getPosition());

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void getInvalidEmployeeById() {
        Long invalidEmployeeId = 999L;

        when(employeeRepository.findById(invalidEmployeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeServiceImpl.getEmployeeById(invalidEmployeeId));

        assertEquals("Employee not found with ID 999", exception.getMessage());

        verify(employeeRepository, times(1)).findById(invalidEmployeeId);
    }

    @Test
    void getAllEmployees() {

        Department department = new Department(1L, "Engineering", "Engineering department");

        Employee employee1 = new Employee(1L, "Jan", "Van Gogh",
                "JanVa@gmail.com", department, "Active", "Developer");

        Employee employee2 = new Employee(2L, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department, "Inactive", "IT");

        List<Employee> employeeList = new ArrayList<>(List.of(employee1, employee2));


        when(employeeRepository.findAll()).thenReturn(employeeList);

        List<EmployeeDto> result = employeeServiceImpl.getAllEmployees();


        assertNotNull(result);

        assertEquals(2, result.size());

        verify(employeeRepository, times(1)).findAll();

        EmployeeDto firstEmployee = result.get(0);

        assertNotNull(firstEmployee);
        assertEquals("Jan", firstEmployee.getFirstName());
        assertEquals("Van Gogh", firstEmployee.getLastName());
        assertEquals("JanVa@gmail.com", firstEmployee.getEmail());
        assertEquals(department.getId(), firstEmployee.getDepartmentId());
        assertEquals("Active", firstEmployee.getStatus());
        assertEquals("Developer", firstEmployee.getPosition());
    }

    @Test
    void getEmptyAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<EmployeeDto> result = employeeServiceImpl.getAllEmployees();

        assertTrue(result.isEmpty());

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getEmployeesByDepartment() {
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void deleteEmployee() {
    }
}