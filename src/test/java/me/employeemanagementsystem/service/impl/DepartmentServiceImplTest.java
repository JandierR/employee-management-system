package me.employeemanagementsystem.service.impl;

import me.employeemanagementsystem.dto.DepartmentDto;
import me.employeemanagementsystem.entity.Department;
import me.employeemanagementsystem.exception.ResourceNotFoundException;
import me.employeemanagementsystem.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentServiceImpl;

    @Test
    void createDepartment() {
        DepartmentDto inputDto = new DepartmentDto(null, "Engineering", "Engineering Description");
        Department savedDepartment = new Department(1L, "Engineering", "Engineering Description");

        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        DepartmentDto resultDto = departmentServiceImpl.createDepartment(inputDto);

        assertNotNull(resultDto);
        assertEquals(1L, resultDto.getId());
        assertEquals("Engineering", resultDto.getName());
        assertEquals("Engineering Description", resultDto.getDescription());

        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void createInvalidDepartment() {
        DepartmentDto inputDto = new DepartmentDto(1L, "Engineering", "Engineering Description");

        when(departmentRepository.existsDepartmentByName("Engineering")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> departmentServiceImpl.createDepartment(inputDto));

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void getDepartmentById() {
        Department mockDepartment = new Department(1L, "Department", "Department Description");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(mockDepartment));

        DepartmentDto department = departmentServiceImpl.getDepartmentById(1L);

        assertNotNull(department);

        assertEquals("Department", department.getName());
        assertEquals("Department Description", department.getDescription());

        verify(departmentRepository, times(1)).findById(1L);

    }

    @Test
    void getInvalidDepartmentById() {

        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentServiceImpl.getDepartmentById(999L));

        assertEquals("Department not found with ID 999", exception.getMessage());

        verify(departmentRepository, times(1)).findById(999L);

    }

    @Test
    void getAllDepartments() {
    }

    @Test
    void updateDepartment() {
    }

    @Test
    void deleteDepartment() {
    }
}