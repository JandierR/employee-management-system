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

        when(employeeRepository.existsByEmail(employeeDto.getEmail())).thenReturn(true);

        //I have to enter when we call findById for department, otherwise it will throw the notFoundException if there's no department.
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        IllegalArgumentException employeeExistException = assertThrows(IllegalArgumentException.class,
                () -> employeeServiceImpl.createEmployee(employeeDto));


        assertNotNull(employeeExistException);

        assertEquals("Employee already exist with email LeoVi@gmail.com", employeeExistException.getMessage());

        verify(employeeRepository, times(1)).existsByEmail(employeeDto.getEmail());
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

        assertNotNull(result);
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
        Long departmentId = 1L;
        Department department = new Department(departmentId, "Engineering", "Engineering department");

        Employee employee1 = new Employee(1L, "Jan", "Van Gogh",
                "JanVa@gmail.com", department, "Active", "Developer");

        Employee employee2 = new Employee(2L, "Leonardo", "Da Vinci",
                "LeoVi@gmail.com", department, "Inactive", "IT");


        List<Employee> employeeList = new ArrayList<>(List.of(employee1, employee2));


        when(departmentRepository.existsById(departmentId)).thenReturn(true);
        when(employeeRepository.findByDepartmentId(departmentId)).thenReturn(employeeList);

        List<EmployeeDto> result = employeeServiceImpl.getEmployeesByDepartment(departmentId);

        assertNotNull(result);

        assertEquals(2, result.size());

        verify(employeeRepository, times(1)).findByDepartmentId(departmentId);

        EmployeeDto secondEmployee = result.get(1);

        assertNotNull(secondEmployee);
        assertEquals("Leonardo", secondEmployee.getFirstName());
        assertEquals("Da Vinci", secondEmployee.getLastName());
        assertEquals("LeoVi@gmail.com", secondEmployee.getEmail());
        assertEquals(department.getId(), secondEmployee.getDepartmentId());
        assertEquals("Inactive", secondEmployee.getStatus());
        assertEquals("IT", secondEmployee.getPosition());
    }

    @Test
    void getEmployeesByDepartmentNotFound() {
        Long invalidDepartmentId = 999L;

        List<Employee> employeeList = new ArrayList<>(List.of());


        when(departmentRepository.existsById(invalidDepartmentId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeServiceImpl.getEmployeesByDepartment(invalidDepartmentId));

        assertNotNull(exception);

        assertTrue(employeeList.isEmpty());

        assertEquals("Department not found with ID 999", exception.getMessage());

        verify(employeeRepository, never()).findByDepartmentId(invalidDepartmentId);
        verify(departmentRepository, times(1)).existsById(invalidDepartmentId);
    }

    @Test
    void updateEmployee() {
        Long departmentId = 1L;
        Long employeeId = 1L;

        Department department = new Department(departmentId, "Engineering", "Engineering department");

        Employee exisitingEmployee = new Employee(employeeId, "Jan", "Van Gogh",
                "JanVa@gmail.com", department, "Active", "Developer");

        Employee updatedEmployee = new Employee(exisitingEmployee.getId(), "Jandier", "Rojas",
                "JanRo@gmail.com", department, "Inactive", "Developer");

        EmployeeDto requestedEmployeeDto = new EmployeeDto(exisitingEmployee.getId(), "Jandier", "Rojas",
                "JanRo@gmail.com", department.getId(), "Inactive", "Developer");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(exisitingEmployee));
        when(departmentRepository.findById(exisitingEmployee.getDepartment().getId())).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeDto result = employeeServiceImpl.updateEmployee(employeeId, requestedEmployeeDto);

        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("Jandier", result.getFirstName());
        assertEquals("Rojas", result.getLastName());
        assertEquals("JanRo@gmail.com", result.getEmail());
        assertEquals(department.getId(), result.getDepartmentId());
        assertEquals("Inactive", result.getStatus());
        assertEquals("Developer", result.getPosition());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(employeeRepository, times(1)).save(any(Employee.class));

    }

    @Test
    void updateEmployeeInvalidEmployeeId() {
        Long departmentId = 1L;
        Long invalidEmployeeId = 999L;


        when(employeeRepository.findById(invalidEmployeeId)).thenReturn(Optional.empty());
        EmployeeDto requestedEmployeeDto = new EmployeeDto(invalidEmployeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", departmentId, "Inactive", "Developer");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeServiceImpl.updateEmployee(invalidEmployeeId, requestedEmployeeDto));

        assertNotNull(exception);
        assertEquals("Employee not found with ID 999", exception.getMessage());

        verify(employeeRepository, times(1)).findById(invalidEmployeeId);
        verify(departmentRepository, never()).findById(departmentId);
        verify(employeeRepository, never()).save(any(Employee.class));

    }

    @Test
    void updateEmployeeInvalidDepartmentId() {
        Long invalidDepartmentId = 999L;
        Long employeeId = 1L;
        Department department = new Department(2L, "Engineering", "Engineering department");

        Employee existingEmployee = new Employee(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", department, "Inactive", "Developer");

        EmployeeDto requestedEmployeeDto = new EmployeeDto(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", invalidDepartmentId, "Inactive", "Developer");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(departmentRepository.findById(invalidDepartmentId)).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeServiceImpl.updateEmployee(employeeId, requestedEmployeeDto));

        assertNotNull(exception);
        assertEquals("Department not found with ID 999", exception.getMessage());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(departmentRepository, times(1)).findById(invalidDepartmentId);
        verify(employeeRepository, never()).save(any(Employee.class));

    }

    @Test
    void deleteEmployee() {
        Long departmentId = 1L;
        Long employeeId = 1L;

        Department department = new Department(departmentId, "Engineering", "Engineering department");

        Employee exisitingEmployee = new Employee(employeeId, "Jan", "Van Gogh",
                "JanVa@gmail.com", department, "Active", "Developer");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(exisitingEmployee));

        employeeServiceImpl.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).delete(exisitingEmployee);
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void deleteEmployeeInvalidId() {
        Long invalidEmployeeId = 999L;

        when(employeeRepository.findById(invalidEmployeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeServiceImpl.deleteEmployee(invalidEmployeeId));

        assertEquals("Employee not found with ID 999", exception.getMessage());

        verify(employeeRepository, never()).delete(any(Employee.class));
        verify(employeeRepository, times(1)).findById(invalidEmployeeId);
    }
}